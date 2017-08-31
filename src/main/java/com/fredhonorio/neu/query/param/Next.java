package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.Exp;
import com.fredhonorio.neu.query.Var;
import com.fredhonorio.neu.query.Statement;
import com.fredhonorio.neu.query.WithType;
import com.fredhonorio.neu.type.*;
import com.fredhonorio.neu.util.Strings;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.LinkedHashSet;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.collection.TreeMap;
import javaslang.control.Option;

import java.util.function.Function;

import static com.fredhonorio.neu.query.param.Fragment.Node.fNode;
import static com.fredhonorio.neu.type.Properties.empty;
import static javaslang.control.Option.none;
import static javaslang.control.Option.some;

public interface Next {

    interface Str extends Fragments {
        default Builder.StrB s(String s) {
            return new Builder.StrB(fragments().append(new Fragment.Str(s)));
        }

        default Builder.StrB s(Exp s) {
            return new Builder.StrB(fragments().append(new Fragment.Str(s.value)));
        }

        @Deprecated
        default Builder.StrB match() {
            return s("MATCH");
        }

        @Deprecated
        default Builder.StrB with() {
            return s("WITH");
        }

        @Deprecated
        default Builder.StrB with(String... parts) {
            return with().s(List.of(parts).mkString(", "));
        }

        @Deprecated
        default Builder.StrB create() {
            return s("CREATE");
        }

        @Deprecated
        default Builder.StrB create(String s) {
            return create().s(s);
        }

        @Deprecated
        default Builder.StrB merge() {
            return s("MERGE");
        }

        @Deprecated
        default Builder.StrB delete() {
            return s("DELETE");
        }

        @Deprecated
        default Builder.StrB merge(String s) {
            return merge().s(s);
        }

        @Deprecated
        default Builder.StrB return_(String s) {
            return s("RETURN").s(s);
        }

        @Deprecated
        default Builder.StrB return_(String... parts) {
            return s("RETURN").s(List.of(parts).mkString(", "));
        }

        //
        default Builder.StrB Match() {
            return s("MATCH");
        }

        default Builder.StrB With() {
            return s("WITH");
        }

        @Deprecated
        default Builder.StrB With(String... parts) {
            return With().s(List.of(parts).mkString(", "));
        }

        default <T extends Exp> Builder.StrB With(T... parts) {
            return With().s(List.of(parts).map(Exp::asString).mkString(", "));
        }

        default Builder.StrB Create() {
            return s("CREATE");
        }

        default Builder.StrB Merge() {
            return s("MERGE");
        }

        default Builder.StrB Merge(String s) {
            return Merge().s(s);
        }

        default Builder.StrB Where() {
            return s("WHERE");
        }

        default Builder.StrB Delete() {
            return s("DELETE");
        }

        default Builder.StrB Return(Exp exp) {
            return s("RETURN").s(exp.asString());
        }

        default Builder.StrB Return(Exp... exps) {
            return s("RETURN").s(List.of(exps).map(Exp::asString).mkString(", "));
        }

        default Builder.StrB Limit(long limit) {
            return s("LIMIT").s(Long.toString(limit));
        }

        default Builder.StrB Skip(long skip) {
            return s("SKIP").s(Long.toString(skip));
        }

        default Builder.StrB inject(Iterable<Fragment> fragments) {
            return new Builder.StrB(fragments().appendAll(fragments));
        }
    }

    interface Node extends Fragments {
        default Builder.NodeB node(Fragment.Node node) {
            return new Builder.NodeB(fragments().append(node));
        }

        default Builder.NodeB node() {
            return node(fNode(none(), LinkedHashSet.empty(), empty()));
        }

        // with var
        default Builder.NodeB node(Var name) {
            return node(fNode(Option.of(name), LinkedHashSet.empty(), empty()));
        }

        default Builder.NodeB node(Var name, Label label) {
            return node(fNode(Option.of(name), LinkedHashSet.of(label), empty()));
        }

        default Builder.NodeB node(Var name, Iterable<Label> labels) {
            return node(fNode(Option.of(name), LinkedHashSet.ofAll(labels), empty()));
        }

        default Builder.NodeB node(Var name, Properties properties) {
            return node(fNode(Option.of(name), LinkedHashSet.empty(), properties));
        }

        default Builder.NodeB node(Var name, Label label, Properties properties) {
            return node(fNode(Option.of(name), LinkedHashSet.of(label), properties));
        }

        default Builder.NodeB node(Var name, Iterable<Label> labels, Properties properties) {
            return node(fNode(Option.of(name), LinkedHashSet.ofAll(labels), properties));
        }

        default Builder.NodeB node(Var name, com.fredhonorio.neu.type.Node node) {
            return node(fNode(Option.of(name), node.labels, node.properties));
        }

        default Builder.NodeB node(Label label) {
            return node(fNode(Option.none(), LinkedHashSet.of(label), empty()));
        }

        default Builder.NodeB node(Iterable<Label> labels) {
            return node(fNode(Option.none(), LinkedHashSet.ofAll(labels), empty()));
        }

        default Builder.NodeB node(Properties properties) {
            return node(fNode(Option.none(), LinkedHashSet.empty(), properties));
        }

        default Builder.NodeB node(Label label, Properties properties) {
            return node(fNode(Option.none(), LinkedHashSet.of(label), properties));
        }

        default Builder.NodeB node(Iterable<Label> labels, Properties properties) {
            return node(fNode(Option.none(), LinkedHashSet.ofAll(labels), properties));
        }

        default Builder.NodeB node(com.fredhonorio.neu.type.Node node) {
            return node(fNode(Option.none(), node.labels, node.properties));
        }

        @Deprecated
        default Builder.NodeB node(String name) {
            return node(fNode(Option.of(Var.of(name)), LinkedHashSet.empty(), empty()));
        }

        @Deprecated
        default Builder.NodeB node(String name, Properties properties) {
            return node(fNode(Option.of(Var.of(name)), LinkedHashSet.empty(), properties));
        }

        @Deprecated
        default Builder.NodeB node(String name, Label label) {
            return node(fNode(Option.of(Var.of(name)), LinkedHashSet.of(label), empty()));
        }

        @Deprecated
        default Builder.NodeB node(String name, Iterable<Label> labels) {
            return node(fNode(Option.of(Var.of(name)), LinkedHashSet.ofAll(labels), empty()));
        }

        @Deprecated
        default Builder.NodeB node(String name, Label label, Properties properties) {
            return node(fNode(Option.of(Var.of(name)), LinkedHashSet.of(label), properties));
        }

        @Deprecated
        default Builder.NodeB node(String name, Iterable<Label> labels, Properties properties) {
            return node(fNode(Option.of(Var.of(name)), LinkedHashSet.ofAll(labels), properties));
        }

        @Deprecated
        default Builder.NodeB node(String name, com.fredhonorio.neu.type.Node node) {
            return node(fNode(Option.of(Var.of(name)), node.labels, node.properties));
        }
    }

    interface From extends Fragments {
        @Deprecated
        default Builder.FromB from(Var name) {
            return new Builder.FromB(fragments().append(new Fragment.Rel(Fragment.Dir.FROM, some(name), none(), empty())));
        }
    }

    interface To extends Fragments {

        default Builder.ToB to() {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), none(), empty())));
        }

        @Deprecated
        default Builder.ToB to(String s) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), some(Type.of(s)), empty())));
        }

        default Builder.ToB to(Type type) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), some(type), empty())));
        }

        @Deprecated
        default Builder.ToB to(String n, String s) {
            return to(Var.of(n), Type.of(s));
        }

        default Builder.ToB to(Var name, WithType withType) {
            return to(name, withType.type());
        }

        default Builder.ToB to(Var name, Type type) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, some(name), some(type), empty())));
        }

        default Builder.ToB to(Var name) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, some(name), none(), empty())));
        }

        default Builder.ToB to(Var name, Properties properties) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, some(name), none(), properties)));
        }


        default Builder.ToB to(Properties props) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), none(), props)));
        }

        default Builder.ToB to(Type type, Properties props) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), some(type), props)));
        }

        default Builder.ToB to(Var name, Type type, Properties props) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, some(name), some(type), props)));
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
                    node -> node.node.properties.nonEmpty() ? 1 : 0,
                    rel -> rel.properties.nonEmpty() ? 1 : 0,
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
            rel -> relFragment(rel, idx),
            param -> Tuple.of("$_" + idx, TreeMap.of("_" + idx, param.parameter))
        );
    }

    static Tuple2<String, TreeMap<String, Parameter>> nodeFragment(Fragment.Node f, int idx) {
        Function<Long, String> propName = propIdx -> Strings.concat("_", Long.toString(idx), "_", Long.toString(propIdx));

        Seq<Tuple2<String, Parameter>> parameterValues = f.node.properties.asMap().zipWithIndex()
            .map(t -> {
                    long index = t._2;
                    Property prop = t._1._2;
                    return Tuple.of(propName.apply(index), Property.asParam(prop));
                }
            );

        return Tuple.of(
            f.pattern(propName),
            TreeMap.ofEntries(parameterValues)
        );
    }

    // TODO: dedup
    static Tuple2<String, TreeMap<String, Parameter>> relFragment(Fragment.Rel f, int idx) {
        Function<Long, String> propName = propIdx -> Strings.concat("_", Long.toString(idx), "_", Long.toString(propIdx));

        Seq<Tuple2<String, Parameter>> parameterValues = f.properties.asMap().zipWithIndex()
            .map(t -> {
                    long index = t._2;
                    Property prop = t._1._2;
                    return Tuple.of(propName.apply(index), Property.asParam(prop));
                }
            );

        return Tuple.of(
            f.pattern(propName),
            TreeMap.ofEntries(parameterValues)
        );
    }
}
