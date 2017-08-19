package com.fredhonorio.neu.query;

public class Ref implements AsString {

    public final String name;

    public Ref(String name) {
        this.name = name;
    }

    public static Ref of(String name) {
        return new Ref(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ref ref = (Ref) o;

        return name != null ? name.equals(ref.name) : ref.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Ref{" +
            "name='" + name + '\'' +
            '}';
    }

    @Override
    public String asString() {
        return name;
    }

    public RefProp dot(Field f) {
        return new RefProp(this, f.name());
    }
}
