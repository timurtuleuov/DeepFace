package com.timur.deepface.entity;

public enum ContentType {
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    }
