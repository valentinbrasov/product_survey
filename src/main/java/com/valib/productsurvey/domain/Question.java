package com.valib.productsurvey.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class Question implements Cloneable {
    private UUID id;
    private String text;
    private Integer orderIndex;

    public Question(UUID id, String text) {
        this.id = id;
        this.text = text;
    }

    public Question(UUID id, String text, Integer orderIndex) {
        this.id = id;
        this.text = text;
        this.orderIndex = orderIndex;
    }

    @Override
    public Question clone() {
        return new Question(this.id, this.text, this.orderIndex);
    }
}
