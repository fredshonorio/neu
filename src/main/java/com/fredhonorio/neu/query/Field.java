package com.fredhonorio.neu.query;

public interface Field {
    String fieldName();

    public static Field of(String fieldName) {
        return () -> fieldName;
    }
}
