package com.example.profile_app.entity;

public enum Availability {
    INTERNSHIP("internship"),
    FULL_TIME("full-time"),
    CONTRACT("contract"),
    NOT_SPECIFIED("not specified");

    private final String value;

    Availability(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Availability fromValue(String value) {
        for (Availability a : values()) {
            if (a.value.equals(value)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Invalid availability: " + value);
    }
}