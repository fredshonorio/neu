package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.ToType;
import com.fredhonorio.neu.query.Var;
import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.Properties;
import com.fredhonorio.neu.type.Relationship;
import com.fredhonorio.neu.type.Type;
import javaslang.collection.LinkedHashSet;
import javaslang.collection.List;
import javaslang.control.Option;

import static com.fredhonorio.neu.query.param.Fragment.Node.fNode;
import static com.fredhonorio.neu.type.Properties.empty;
import static javaslang.control.Option.none;
import static javaslang.control.Option.some;

public class Path {

    private final List<Fragment> fragments;

    private Path(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public final List<Fragment> fragments() {
        return fragments;
    }

    private Path append(Fragment node) {
        return new Path(fragments.append(node));
    }

    /*
            append node
     */

    public Path node() {
        return append(fNode(none(), LinkedHashSet.empty(), empty()));
    }

    public Path node(Var name) {
        return append(fNode(Option.of(name), LinkedHashSet.empty(), empty()));
    }

    public Path node(Var name, Label label) {
        return append(fNode(Option.of(name), LinkedHashSet.of(label), empty()));
    }

    public Path node(Var name, Iterable<Label> labels) {
        return append(fNode(Option.of(name), LinkedHashSet.ofAll(labels), empty()));
    }

    public Path node(Var name, Properties properties) {
        return append(fNode(Option.of(name), LinkedHashSet.empty(), properties));
    }

    public Path node(Var name, Label label, Properties properties) {
        return append(fNode(Option.of(name), LinkedHashSet.of(label), properties));
    }

    public Path node(Var name, Iterable<Label> labels, Properties properties) {
        return append(fNode(Option.of(name), LinkedHashSet.ofAll(labels), properties));
    }

    public Path node(Var name, com.fredhonorio.neu.type.Node node) {
        return append(fNode(Option.of(name), node.labels, node.properties));
    }

    public Path node(Label label) {
        return append(fNode(Option.none(), LinkedHashSet.of(label), empty()));
    }

    public Path node(Iterable<Label> labels) {
        return append(fNode(Option.none(), LinkedHashSet.ofAll(labels), empty()));
    }

    public Path node(Properties properties) {
        return append(fNode(Option.none(), LinkedHashSet.empty(), properties));
    }

    public Path node(Label label, Properties properties) {
        return append(fNode(Option.none(), LinkedHashSet.of(label), properties));
    }

    public Path node(Iterable<Label> labels, Properties properties) {
        return append(fNode(Option.none(), LinkedHashSet.ofAll(labels), properties));
    }

    public Path node(com.fredhonorio.neu.type.Node node) {
        return append(fNode(Option.none(), node.labels, node.properties));
    }

    /*
            aapend to
     */

    public Path to() {
        return append(new Fragment.Rel(Fragment.Dir.TO, none(), List.empty(), empty()));
    }

    public Path to(Type type) {
        return append(new Fragment.Rel(Fragment.Dir.TO, none(), List.of(type), empty()));
    }

    public Path to(Iterable<Type> types) {
        return append(new Fragment.Rel(Fragment.Dir.TO, none(), List.ofAll(types), empty()));
    }

    public Path to(ToType type) {
        return append(new Fragment.Rel(Fragment.Dir.TO, none(), List.of(type.type()), empty()));
    }

    public Path to(Var name, Type type) {
        return append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.of(type), empty()));
    }

    public Path to(Var name, Iterable<Type> types) {
        return append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.ofAll(types), empty()));
    }

    public Path to(Var name, ToType toType) {
        return to(name, toType.type());
    }

    public Path to(Var name) {
        return append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.empty(), empty()));
    }

    public Path to(Var name, Properties properties) {
        return append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.empty(), properties));
    }

    public Path to(Var name, Type type, Properties props) {
        return append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.of(type), props));
    }

    public Path to(Var name, Iterable<Type> types, Properties props) {
        return append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.ofAll(types), props));
    }

    public Path to(Var name, ToType type, Properties props) {
        return to(name, type.type(), props);
    }

    public Path to(Var name, Relationship relationship) {
        return append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.of(relationship.type), relationship.properties));
    }

    public Path to(Properties props) {
        return append(new Fragment.Rel(Fragment.Dir.TO, none(), List.empty(), props));
    }

    public Path to(Type type, Properties props) {
        return append(new Fragment.Rel(Fragment.Dir.TO, none(), List.of(type), props));
    }

    public Path to(Iterable<Type> types, Properties props) {
        return append(new Fragment.Rel(Fragment.Dir.TO, none(), List.ofAll(types), props));
    }

    public Path to(ToType type, Properties props) {
        return to(type.type(), props);
    }

    public Path to(Relationship relationship) {
        return append(new Fragment.Rel(Fragment.Dir.TO, none(), List.of(relationship.type), relationship.properties));
    }

    public static Path of(List<Fragment> fragments) {
        return new Path(fragments);
    }


}