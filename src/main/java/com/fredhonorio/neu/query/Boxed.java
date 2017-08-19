package com.fredhonorio.neu.query;

public abstract class Boxed<T extends Comparable<T>> {

    public final T value;

    protected Boxed(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Boxed<?> boxed = (Boxed<?>) o;

        return value != null ? value.equals(boxed.value) : boxed.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        String clz = getClass().getSimpleName();
        return clz + "{" + value + '}';
    }
}
