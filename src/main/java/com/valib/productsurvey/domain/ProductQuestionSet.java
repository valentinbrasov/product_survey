package com.valib.productsurvey.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class ProductQuestionSet {
    private UUID productId;
    private Map<UUID, Question> questions = new HashMap<>();
    private Object lock = new Object();

    public Question getQuestion(UUID questionId) {
        return questions.get(questionId);
    }

    public void putQuestion(Question question) {
        questions.put(question.getId(), question);
    }

    public ProductQuestionSet(UUID productId) {
        this.productId = productId;
    }
}
