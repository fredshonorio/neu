package com.fredhonorio.neu.type;

import com.fredhonorio.neu.query.Boxed;
import javaslang.collection.List;

public class Label extends Boxed<String> implements Comparable<Label> {

    private Label(String value) {
        super(value);
    }

    public static Label of(String value) {
        return new Label(sanitize(value));
    }

    public static Label label(String value) {
        return of(value);
    }

    public static List<Label> many(String... values) {
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
