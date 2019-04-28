package com.valib.productsurvey;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.valib.productsurvey.domain.Product;
import com.valib.productsurvey.domain.ProductQuestionSet;
import com.valib.productsurvey.domain.Question;
import com.valib.productsurvey.internal.ExceptionUtil;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ProductSurveyAdminService {
    private final Map<UUID, Product> products;

    private final LoadingCache<UUID, ProductQuestionSet> productQuestionSetStore = CacheBuilder.newBuilder().build(
            new CacheLoader<UUID, ProductQuestionSet>() {
                @Override
                public ProductQuestionSet load(UUID productId) {
                    if (!products.containsKey(productId)) {
                        throw new IllegalArgumentException("Product " + productId + " does not exist");
                    }
                    return new ProductQuestionSet(productId);
                }
            });

    public ProductSurveyAdminService(Map<UUID, Product> products) {
        this.products = products;
    }

    public ProductQuestionSet getQuestionSetForProduct(UUID productId) {
        try {
            return productQuestionSetStore.get(productId);
        } catch (Exception e) {
            throw ExceptionUtil.toRuntimeException(e);
        }
    }

    public Question appendQuestion(UUID productId, String questionText) {
        ProductQuestionSet productQuestionSet = getQuestionSetForProduct(productId);
        Question question = new Question(UUID.randomUUID(), questionText);

        synchronized (productQuestionSet.getLock()) {
            int maxOrderIndex = productQuestionSet.getQuestions().isEmpty() ? 0
                    : productQuestionSet.getQuestions().values().stream().mapToInt(q -> q.getOrderIndex()).max().getAsInt();
            productQuestionSet.putQuestion(question);
            question.setOrderIndex(maxOrderIndex + 1);
        }

        return question.clone();
    }

    public void swapQuestions(UUID productId, Question q1, Question q2) {
        ProductQuestionSet productQuestionSet = getQuestionSetForProduct(productId);

        synchronized (productQuestionSet.getLock()) {
            Question q1Existing = productQuestionSet.getQuestion(q1.getId());
            if (!q1.getOrderIndex().equals(q1Existing.getOrderIndex())) {
                throw new IllegalArgumentException("Question " + q1.getId() + " was updated in the meanwhile");
            }
            Question q2Existing = productQuestionSet.getQuestion(q2.getId());
            if (!q2.getOrderIndex().equals(q2Existing.getOrderIndex())) {
                throw new IllegalArgumentException("Question " + q2.getId() + " was updated in the meanwhile");
            }

            int tmp = q1Existing.getOrderIndex();
            q1Existing.setOrderIndex(q2Existing.getOrderIndex());
            q2Existing.setOrderIndex(tmp);

            q1.setOrderIndex(q1Existing.getOrderIndex());
            q2.setOrderIndex(q2Existing.getOrderIndex());
        }
    }

    public void updateQuestionText(UUID productId, UUID questionId, String questionText) {
        if (productId == null) {
            throw new IllegalArgumentException("product ID cannot be null for updating question");
        }
        if (questionId == null) {
            throw new IllegalArgumentException("question ID cannot be null for updating question");
        }
        String cleansedQuestionText = questionText == null ? null : questionText.trim();
        if (cleansedQuestionText == null || cleansedQuestionText.isEmpty()) {
            throw new IllegalArgumentException("question text cannot be null or blank wehn updating question");
        }

        ProductQuestionSet productQuestionSet = getQuestionSetForProduct(productId);

        synchronized (productQuestionSet.getLock()) {
            Question qExisting = productQuestionSet.getQuestion(questionId);
            if (qExisting == null) {
                throw new IllegalArgumentException("Question " + questionId+ " does not exist");
            }
            qExisting.setText(cleansedQuestionText);
        }
    }

    public void deleteQuestion(UUID productId, UUID questionId) {
        ProductQuestionSet productQuestionSet = getQuestionSetForProduct(productId);
        synchronized (productQuestionSet.getLock()) {
            if(productQuestionSet.getQuestions().remove(questionId) == null) {
                throw new IllegalArgumentException("Question " + questionId+ " does not exist");
            }
        }
    }
}
