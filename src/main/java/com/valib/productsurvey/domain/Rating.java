package com.valib.productsurvey.domain;

public enum Rating {
    VERY_POOR(1),
    POOR(2),
    SO_SO(3),
    GOOD(4),
    VERY_GOOD(5);

    private int ratingNumber;

    private Rating(int ratingNumber) {
        this.ratingNumber = ratingNumber;
    }
}
