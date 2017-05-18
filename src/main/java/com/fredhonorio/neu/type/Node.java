package com.fredhonorio.neu.type;

import javaslang.collection.LinkedHashSet;
import com.fredhonorio.neu.model.IdAnd;

public class Node {

    public final LinkedHashSet<Label> labels;
    public final Properties properties;

    public Node(LinkedHashSet<Label> labels, Properties properties) {
        this.labels = labels;
        this.properties = properties;
    }

    public static Node node(Label label, Properties properties) {
        return new Node(LinkedHashSet.of(label), properties);
    }

    public static Node node(Iterable<Label> labels, Properties properties) {
        return new Node(LinkedHashSet.ofAll(labels), properties);
    }

    public IdAnd<Node> withId(long id) {
        return new IdAnd<>(id, this);
    }

    public String describe() {
        return labels.map(e -> e.value).mkString(":")
            + ", "
            + properties.describe();
    }

    @Override
    public String toString() {
        return "Node(" + describe() + ')';
    }
}
