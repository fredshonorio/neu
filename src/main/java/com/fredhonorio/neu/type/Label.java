package com.fredhonorio.neu.type;

import javaslang.collection.List;

public class Label implements Comparable<Label> {
    public final String value;

    private Label(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label label = (Label) o;

        return value != null ? value.equals(label.value) : label.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Label{" +
            "value='" + value + '\'' +
            '}';
    }

    public static Label of(String value) {
        return new Label(sanitize(value));
    }

    public static List<Label> many(String...values) {
        return List.of(values).map(Label::of);
    }

    public static String sanitize(String labelValue) {
        return labelValue;
    }

    @Override
    public int compareTo(Label label) {
        return this.value.compareTo(label.value);
    }
}
