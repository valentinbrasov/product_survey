package com.valib.productsurvey.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class Product {
    private UUID id;
    private String name;

    public Product(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
