package com.valib.productsurvey;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.valib.productsurvey.domain.Answer;
import com.valib.productsurvey.domain.Product;
import com.valib.productsurvey.domain.ProductQuestionSet;
import com.valib.productsurvey.domain.UserProductSurvey;
import com.valib.productsurvey.internal.ExceptionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ProductSurveyService {
    private Map<UUID, Product> products;

    private ProductSurveyAdminService surveyAdminService;

    public ProductSurveyService(Map<UUID, Product> products,
            ProductSurveyAdminService surveyAdminService) {
        this.products = products;
        this.surveyAdminService = surveyAdminService;
    }

    /**
     * Outer key: product ID. Inner key: user ID.
     */
    @VisibleForTesting
    final LoadingCache<UUID, Map<UUID, UserProductSurvey>> userProductSurveyStore = CacheBuilder.newBuilder().build(
            new CacheLoader<UUID, Map<UUID, UserProductSurvey>>() {
                @Override
                public Map<UUID, UserProductSurvey> load(UUID productId) {
                    return new HashMap<>();
                }
            });

    public UserProductSurvey createEmptyUserProductSurvey(UUID userId, UUID productId) {
        ProductQuestionSet productQuestionSet = surveyAdminService.getQuestionSetForProduct(productId);
        UserProductSurvey userProductSurvey = new UserProductSurvey(userId, products.get(productId));
        synchronized (productQuestionSet.getLock()) {
            productQuestionSet.getQuestions().values().forEach(question -> userProductSurvey.putAnswer(new Answer(question.clone())));
        }
        return userProductSurvey;
    }

    /**
     * This overwrites an existing survey for an user and a product.
     */
    public void saveUserProductSurvey(UserProductSurvey userProductSurvey) {
        try {
            userProductSurveyStore
                    .get(userProductSurvey.getProduct().getId()).put(userProductSurvey.getUserId(), userProductSurvey);
        } catch (Exception e) {
            throw ExceptionUtil.toRuntimeException(e);
        }
    }

    public UserProductSurvey getUserProductSurvey(UUID userId, UUID productId) {
        try {
            return userProductSurveyStore.get(productId).get(userId);
        } catch (ExecutionException e) {
            throw ExceptionUtil.toRuntimeException(e);
        }
    }
}
