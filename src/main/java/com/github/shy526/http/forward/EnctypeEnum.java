package com.github.shy526.http.forward;

public enum EnctypeEnum {
    MULTIPART("Multipart/form-data"),
    URLENCODED("application/x-www-form-urlencoded"),
    JSON("application/json"),
    ;

    EnctypeEnum(String val) {
        this.val = val;
    }

    private String val;

    public String getVal() {
        return val;
    }
}
