package com.valib.productsurvey.domain;

import com.valib.productsurvey.domain.Answer;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class UserProductSurvey {
    private UUID userId;
    private Product product;
    /**
     * Key: question ID.
     */
    private Map<UUID, Answer> answers = new HashMap<>();

    public UserProductSurvey(UUID userId, Product product) {
        this.userId = userId;
        this.product = product;
    }

    public Answer putAnswer(Answer answer) {
        return answers.put(answer.getQuestion().getId(), answer);
    }
}
