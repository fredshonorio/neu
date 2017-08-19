package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.AsString;
import com.fredhonorio.neu.query.Ref;
import com.fredhonorio.neu.query.Statement;
import com.fredhonorio.neu.type.*;
import javaslang.*;
import javaslang.collection.LinkedHashSet;
import javaslang.collection.List;
import javaslang.collection.Tree;
import javaslang.collection.TreeMap;
import javaslang.control.Option;

import java.util.function.Function;

import static com.fredhonorio.neu.query.param.Fragment.Node.fNode;
import static javaslang.collection.LinkedHashSet.empty;
import static javaslang.control.Option.none;
import static javaslang.control.Option.some;

public interface Next {

    interface Str extends Fragments {
        default Builder.StrB s(String s) {
            return new Builder.StrB(fragments().append(new Fragment.Str(s)));
        }

        default Builder.StrB match() {
            return s("MATCH");
        }

        default Builder.StrB with() {
            return s("WITH");
        }

        default Builder.StrB with(String... parts) {
            return with().s(List.of(parts).mkString(", "));
        }

        default Builder.StrB create() {
            return s("CREATE");
        }

        default Builder.StrB create(String s) {
            return create().s(s);
        }

        default Builder.StrB merge() {
            return s("MERGE");
        }

        default Builder.StrB delete() {
            return s("DELETE");
        }

        default Builder.StrB merge(String s) {
            return merge().s(s);
        }

        default Builder.StrB return_(String s) {
            return s("RETURN").s(s);
        }

        default Builder.StrB return_(String...parts) {
            return s("RETURN").s(List.of(parts).mkString(", "));
        }
    }

    interface Node extends Fragments {
        default Builder.NodeB node(Fragment.Node node) {
            return new Builder.NodeB(fragments().append(node));
        }

        default Builder.NodeB node() {
            return node(fNode(none(), empty(), Properties.empty()));
        }

        default Builder.NodeB node(String name) {
            return node(fNode(Option.of(name), empty(), Properties.empty()));
        }

        default Builder.NodeB node(Label label) {
            return node(fNode(Option.none(), LinkedHashSet.of(label), Properties.empty()));
        }

        default Builder.NodeB node(Iterable<Label> labels) {
            return node(fNode(Option.none(), LinkedHashSet.ofAll(labels), Properties.empty()));
        }

        default Builder.NodeB node(Label label, Properties properties) {
            return node(fNode(Option.none(), LinkedHashSet.of(label), properties));
        }

        default Builder.NodeB node(Iterable<Label> labels, Properties properties) {
            return node(fNode(Option.none(), LinkedHashSet.ofAll(labels), properties));
        }

        default Builder.NodeB node(Properties properties) {
            return node(fNode(none(), empty(), properties));
        }

        default Builder.NodeB node(String name, Properties properties) {
            return node(fNode(Option.of(name), empty(), properties));
        }

        @Deprecated
        default Builder.NodeB node(String name, Label label) {
            return node(fNode(Option.of(name), LinkedHashSet.of(label), Properties.empty()));
        }

        default Builder.NodeB node(Ref name, Label label) {
            return node(fNode(Option.of(name.asString()), LinkedHashSet.of(label), Properties.empty()));
        }

        @Deprecated
        default Builder.NodeB node(String name, Iterable<Label> labels) {
            return node(fNode(Option.of(name), LinkedHashSet.ofAll(labels), Properties.empty()));
        }

        default Builder.NodeB node(Ref name, Iterable<Label> labels) {
            return node(fNode(Option.of(name.asString()), LinkedHashSet.ofAll(labels), Properties.empty()));
        }

        @Deprecated
        default Builder.NodeB node(String name, Label label, Properties properties) {
            return node(fNode(Option.of(name), LinkedHashSet.of(label), properties));
        }

        default Builder.NodeB node(Ref name, Label label, Properties properties) {
            return node(fNode(Option.of(name.asString()), LinkedHashSet.of(label), properties));
        }

        @Deprecated
        default Builder.NodeB node(String name, Iterable<Label> labels, Properties properties) {
            return node(fNode(Option.of(name), LinkedHashSet.ofAll(labels), properties));
        }

        default Builder.NodeB node(Ref name, Iterable<Label> labels, Properties properties) {
            return node(fNode(Option.of(name.asString()), LinkedHashSet.ofAll(labels), properties));
        }

        @Deprecated
        default Builder.NodeB node(String name, com.fredhonorio.neu.type.Node node) {
            return node(fNode(Option.of(name), node.labels, node.properties));
        }

        default Builder.NodeB node(Ref name, com.fredhonorio.neu.type.Node node) {
            return node(fNode(Option.of(name.asString()), node.labels, node.properties));
        }

        default Builder.NodeB node(com.fredhonorio.neu.type.Node node) {
            return node(fNode(Option.none(), node.labels, node.properties));
        }
    }

    interface From extends Fragments {
        default Builder.FromB from(Ref name) {
            return new Builder.FromB(fragments().append(new Fragment.Rel(Fragment.Dir.FROM, Option.some(name), none())));
        }

        @Deprecated
        default Builder.FromB from(String s) {
            return new Builder.FromB(fragments().append(new Fragment.Rel(Fragment.Dir.FROM, Option.some(Ref.of(s)), none())));
        }
    }

    interface To extends Fragments {
        @Deprecated
        default Builder.ToB to(String s) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), Option.some(Type.of(s)))));
        }

        default Builder.ToB to(Type type) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), Option.some(type))));
        }

        @Deprecated
        default Builder.ToB to(String n, String s) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, some(Ref.of(n)), Option.some(Type.of(s)))));
        }

        default Builder.ToB to(Ref name, Type type) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, some(name), Option.some(type))));
        }
    }

    interface Param extends Fragments {
        default Builder.ParamB param(Parameter param) {
            return new Builder.ParamB(fragments().append(new Fragment.Param(param)));
        }
    }

    interface Final extends Fragments {
        default Statement build() {

            List<Fragment> fragments = fragments();

            List<Integer> indices = fragments
                .scanLeft(0, (z, f) -> z + f.match(
                    str -> 0,
                    node -> 1,
                    rel -> 0,
                    param -> 1
                ));

            List<Tuple2<String, TreeMap<String, Parameter>>> parts = fragments.zip(indices)
                .map(x -> fragment(x._1, x._2));

            String query = parts.map(Tuple2::_1).mkString(" ");
            TreeMap<String, Parameter> params = parts.map(Tuple2::_2).flatMap(javaslang.Value::toList).transform(TreeMap::ofEntries);

            return new Statement(query, new NParamMap(params));
        }

        default String show() {
            return null;
        }
    }

    static Tuple2<String, TreeMap<String, Parameter>> fragment(Fragment f, int idx) {
        TreeMap<String, Parameter> empty = TreeMap.empty();
        return f.match(
            str -> Tuple.of(str.s, empty),
            node -> nodeFragment(node, idx),
            rel -> Tuple.of(rel.pattern(), empty),
            param -> Tuple.of("$_" + idx, TreeMap.of("_" + idx, param.parameter))
        );
    }

    static Tuple2<String, TreeMap<String, Parameter>> nodeFragment(Fragment.Node f, int idx) {
        Function<String, String> propName = prop -> "_" + idx + "_" + prop.replace('-', '_'); // TODO: fix escaping this as a reference

        List<Tuple2<String, Parameter>> parameters = f.node.properties.asMap().toList()
            .map(kv -> kv.map(propName, Property::asParam));

        return Tuple.of(
            f.pattern(propName),
            TreeMap.ofEntries(parameters)
        );
    }

}
