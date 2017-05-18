package com.fredhonorio.neu.model;

public class IdAnd<T> {

    public final long id;
    public final T value;

    public IdAnd(long id, T value) {
        this.id = id;
        this.value = value;
    }

    public Id<T> id() {
        return new Id<>(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdAnd<?> idAnd = (IdAnd<?>) o;

        if (id != idAnd.id) return false;
        return value != null ? value.equals(idAnd.value) : idAnd.value == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
