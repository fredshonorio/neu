package com.fredhonorio.neu.type;

import com.fredhonorio.neu.util.Strings;
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
        return labels.map(e -> e.value).toList().transform(Strings.mkString(":"))
            + ", "
            + properties.describe();
    }

    @Override
    public String toString() {
        return "Node(" + describe() + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (labels != null ? !labels.equals(node.labels) : node.labels != null) return false;
        return properties != null ? properties.equals(node.properties) : node.properties == null;
    }

    @Override
    public int hashCode() {
        int result = labels != null ? labels.hashCode() : 0;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
}
