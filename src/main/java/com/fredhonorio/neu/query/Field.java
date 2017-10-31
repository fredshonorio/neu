package com.fredhonorio.neu.query;

public interface Field extends ToField {
    String fieldName();

    public static Field of(String fieldName) {
        return () -> fieldName;
    }

    @Override
    default Field field() {
        return this;
    }
}
