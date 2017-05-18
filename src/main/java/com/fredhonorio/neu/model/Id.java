package com.fredhonorio.neu.model;

public class Id<T> {

    public final long value;

    protected Id(long value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Id<?> id1 = (Id<?>) o;

        return value == id1.value;
    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }
}
