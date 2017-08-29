package com.fredhonorio.neu.type;

public class Type {

    public final String type;

    private Type(String type) {
        // TODO: sanitize type
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Type type1 = (Type) o;

        return type != null ? type.equals(type1.type) : type1.type == null;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Type{" +
            "type='" + type + '\'' +
            '}';
    }

    public static Type of(String type) {
        return new Type(type);
    }
}
