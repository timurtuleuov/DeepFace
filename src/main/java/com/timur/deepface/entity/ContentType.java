package com.timur.deepface.entity;

public enum ContentType {
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    TEXT_PLAIN("text/plain");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    }
