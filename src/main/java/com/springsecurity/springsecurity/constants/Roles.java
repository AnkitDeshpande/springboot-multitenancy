package com.springsecurity.springsecurity.constants;

public enum Roles {
    ADMIN("ADMIN"), USER("USER");

    private String value;

    Roles(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
