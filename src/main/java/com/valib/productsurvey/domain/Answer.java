package com.valib.productsurvey.domain;

import lombok.Getter;

@Getter
public class Answer {
    private Question question;
    private Rating rating;

    // TODO: add a user's comment perhaps

    public Answer(Question question) {
        this.question = question;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }
}
