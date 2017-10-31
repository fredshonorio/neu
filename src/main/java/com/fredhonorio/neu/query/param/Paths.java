package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.ToLabel;
import com.fredhonorio.neu.query.Var;
import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.Properties;
import javaslang.collection.LinkedHashSet;
import javaslang.collection.List;
import javaslang.control.Option;

import static com.fredhonorio.neu.query.param.Fragment.Node.fNode;
import static com.fredhonorio.neu.type.Properties.empty;
import static javaslang.control.Option.none;

public class Paths {
    private static Path node(Fragment.Node node) {
        return Path.of(List.of(node));
    }

    public static Path node() {
        return node(fNode(none(), LinkedHashSet.empty(), empty()));
    }

    public static Path node(Var name) {
        return node(fNode(Option.of(name), LinkedHashSet.empty(), empty()));
    }

    public static Path node(Var name, Label label) {
        return node(fNode(Option.of(name), LinkedHashSet.of(label), empty()));
    }

    public static Path node(Var name, ToLabel label) {
        return node(fNode(Option.of(name), LinkedHashSet.of(label.label()), empty()));
    }

    public static Path node(Var name, Iterable<Label> labels) {
        return node(fNode(Option.of(name), LinkedHashSet.ofAll(labels), empty()));
    }

    public static Path node(Var name, Properties properties) {
        return node(fNode(Option.of(name), LinkedHashSet.empty(), properties));
    }

    public static Path node(Var name, Label label, Properties properties) {
        return node(fNode(Option.of(name), LinkedHashSet.of(label), properties));
    }

    public static Path node(Var name, ToLabel label, Properties properties) {
        return node(fNode(Option.of(name), LinkedHashSet.of(label.label()), properties));
    }

    public static Path node(Var name, Iterable<Label> labels, Properties properties) {
        return node(fNode(Option.of(name), LinkedHashSet.ofAll(labels), properties));
    }

    public static Path node(Var name, com.fredhonorio.neu.type.Node node) {
        return node(fNode(Option.of(name), node.labels, node.properties));
    }

    public static Path node(Label label) {
        return node(fNode(Option.none(), LinkedHashSet.of(label), empty()));
    }

    public static Path node(ToLabel label) {
        return node(fNode(Option.none(), LinkedHashSet.of(label.label()), empty()));
    }

    public static Path node(Iterable<Label> labels) {
        return node(fNode(Option.none(), LinkedHashSet.ofAll(labels), empty()));
    }

    public static Path node(Properties properties) {
        return node(fNode(Option.none(), LinkedHashSet.empty(), properties));
    }

    public static Path node(Label label, Properties properties) {
        return node(fNode(Option.none(), LinkedHashSet.of(label), properties));
    }

    public static Path node(ToLabel label, Properties properties) {
        return node(fNode(Option.none(), LinkedHashSet.of(label.label()), properties));
    }

    public static Path node(Iterable<Label> labels, Properties properties) {
        return node(fNode(Option.none(), LinkedHashSet.ofAll(labels), properties));
    }

    public static Path node(com.fredhonorio.neu.type.Node node) {
        return node(fNode(Option.none(), node.labels, node.properties));
    }

}
