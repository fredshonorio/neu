package com.fredhonorio.neu.type;

public class Relationship {

    public final Type type;
    public final Properties properties;

    public Relationship(Type type, Properties properties) {
        this.type = type;
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "Relationship{" +
            "type='" + type + '\'' +
            ", properties=" + properties +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relationship that = (Relationship) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return properties != null ? properties.equals(that.properties) : that.properties == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
}
