package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.Exp;
import com.fredhonorio.neu.query.Statement;
import com.fredhonorio.neu.query.ToType;
import com.fredhonorio.neu.query.Var;
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
import static com.fredhonorio.neu.query.param.Fragment.str;
import static com.fredhonorio.neu.type.Properties.empty;
import static com.fredhonorio.neu.util.Strings.concat;
import static javaslang.control.Option.none;
import static javaslang.control.Option.some;

@SuppressWarnings("unused")
public class Builder implements Fragments {

    final List<Fragment> fragments;

    private Builder(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public static Builder builder() {
        return new Builder(List.empty());
    }

    @Override
    public List<Fragment> fragments() {
        return fragments;
    }

    public Builder s(String s) {
        return new Builder(fragments().append(new Fragment.Str(s)));
    }

    private Builder f(List<Fragment> frags) {
        return new Builder(fragments().appendAll(frags));
    }

    public Builder s(Exp s) {
        return new Builder(fragments().appendAll(s.fragments));
    }

    public Builder Match() {
        return s("MATCH");
    }

    public Builder Match(Path...paths) {
        return s("MATCH").f(
            List.of(paths)
                .map(p -> p.fragments())
                .intersperse(List.of(str(",")))
                .flatMap(Function.identity()));
    }

    public Builder With() {
        return s("WITH");
    }

    @SafeVarargs
    public final <T extends Exp> Builder With(T... parts) {
        return With().f(
            List.of(parts)
                .map(exp -> exp.fragments)
                .intersperse(List.of(str(",")))
                .flatMap(Function.identity()));
    }

    public Builder Create() {
        return s("CREATE");
    }

    public Builder Merge() {
        return s("MERGE");
    }

    public Builder Where() {
        return s("WHERE");
    }

    public Builder Where(Exp e) {
        return Where().s(e);
    }

    public Builder Set() {
        return s("SET");
    }

    public Builder Set(Exp...exps) {
        return Set().f(
                List.of(exps)
                    .map(exp -> exp.fragments)
                    .intersperse(List.of(str(",")))
                    .flatMap(Function.identity()));
    }

    public Builder Delete() {
        return s("DELETE");
    }

    public Builder Return(Exp exp) {
        return s("RETURN").s(exp);
    }

    public Builder Return(Exp... exps) {
        return s("RETURN").f(
            List.of(exps)
                .map(exp -> exp.fragments)
                .intersperse(List.of(str(",")))
                .flatMap(Function.identity()));
    }

    public Builder Limit(long limit) {
        return s("LIMIT").s(Long.toString(limit));
    }

    public Builder Skip(long skip) {
        return s("SKIP").s(Long.toString(skip));
    }

    public Builder OrderBy(Iterable<OrderByClause> clauses) {
        List<Fragment> frags = List.ofAll(clauses)
            .map(c -> c.exp.fragments.append(str(c.order.cypher())))
            .intersperse(List.of(str(",")))
            .flatMap(Function.identity());

        return s("ORDER BY").f(frags);
    }

    public Builder OrderBy(OrderByClause clause) {
        return OrderBy(List.of(clause));
    }

    public Builder OrderBy(OrderByClause... clauses) {
        return OrderBy(List.of(clauses));
    }

    public Builder OrderBy(Exp exp) {
        return OrderBy(List.of(OrderByClause.asc(exp)));
    }

    public Builder OrderBy(Exp exp, OrderByClause.Order order) {
        return OrderBy(List.of(new OrderByClause(order, exp)));
    }

    public Builder inject(Iterable<Fragment> fragments) {
        return f(List.ofAll(fragments));
    }

    public Builder inject(Fragment... fragments) {
        return f(List.of(fragments));
    }

    private Builder node(Fragment.Node node) {
        return new Builder(fragments().append(node));
    }

    public Builder node() {
        return node(fNode(none(), LinkedHashSet.empty(), empty()));
    }

    // with var
    public Builder node(Var name) {
        return node(fNode(Option.of(name), LinkedHashSet.empty(), empty()));
    }

    public Builder node(Var name, Label label) {
        return node(fNode(Option.of(name), LinkedHashSet.of(label), empty()));
    }

    public Builder node(Var name, Iterable<Label> labels) {
        return node(fNode(Option.of(name), LinkedHashSet.ofAll(labels), empty()));
    }

    public Builder node(Var name, Properties properties) {
        return node(fNode(Option.of(name), LinkedHashSet.empty(), properties));
    }

    public Builder node(Var name, Label label, Properties properties) {
        return node(fNode(Option.of(name), LinkedHashSet.of(label), properties));
    }

    public Builder node(Var name, Iterable<Label> labels, Properties properties) {
        return node(fNode(Option.of(name), LinkedHashSet.ofAll(labels), properties));
    }

    public Builder node(Var name, com.fredhonorio.neu.type.Node node) {
        return node(fNode(Option.of(name), node.labels, node.properties));
    }

    public Builder node(Label label) {
        return node(fNode(Option.none(), LinkedHashSet.of(label), empty()));
    }

    public Builder node(Iterable<Label> labels) {
        return node(fNode(Option.none(), LinkedHashSet.ofAll(labels), empty()));
    }

    public Builder node(Properties properties) {
        return node(fNode(Option.none(), LinkedHashSet.empty(), properties));
    }

    public Builder node(Label label, Properties properties) {
        return node(fNode(Option.none(), LinkedHashSet.of(label), properties));
    }

    public Builder node(Iterable<Label> labels, Properties properties) {
        return node(fNode(Option.none(), LinkedHashSet.ofAll(labels), properties));
    }

    public Builder node(com.fredhonorio.neu.type.Node node) {
        return node(fNode(Option.none(), node.labels, node.properties));
    }

    /*
     *
     *   TO
     *
     */

    public Builder to() {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), List.empty(), empty())));
    }

    public Builder to(Type type) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), List.of(type), empty())));
    }

    public Builder to(Iterable<Type> types) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), List.ofAll(types), empty())));
    }

    public Builder to(ToType type) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), List.of(type.type()), empty())));
    }

    public Builder to(Var name, Type type) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.of(type), empty())));
    }

    public Builder to(Var name, Iterable<Type> types) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.ofAll(types), empty())));
    }

    public Builder to(Var name, ToType toType) {
        return to(name, toType.type());
    }

    public Builder to(Var name) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.empty(), empty())));
    }

    public Builder to(Var name, Properties properties) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.empty(), properties)));
    }

    public Builder to(Var name, Type type, Properties props) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.of(type), props)));
    }

    public Builder to(Var name, Iterable<Type> types, Properties props) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.ofAll(types), props)));
    }

    public Builder to(Var name, ToType type, Properties props) {
        return to(name, type.type(), props);
    }

    public Builder to(Var name, Relationship relationship) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, some(name), List.of(relationship.type), relationship.properties)));
    }

    public Builder to(Properties props) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), List.empty(), props)));
    }

    public Builder to(Type type, Properties props) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), List.of(type), props)));
    }

    public Builder to(Iterable<Type> types, Properties props) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), List.ofAll(types), props)));
    }

    public Builder to(ToType type, Properties props) {
        return to(type.type(), props);
    }

    public Builder to(Relationship relationship) {
        return new Builder(fragments().append(new Fragment.Rel(Fragment.Dir.TO, none(), List.of(relationship.type), relationship.properties)));
    }

    public Builder param(Parameter param) {
        return new Builder(fragments().append(new Fragment.Param(param)));
    }

    public Statement build() {

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

        String query = parts.map(Tuple2::_1).transform(Strings.mkString(" "));
        TreeMap<String, Parameter> params = parts.map(Tuple2::_2).flatMap(javaslang.Value::toList).transform(TreeMap::ofEntries);

        return new Statement(query, new NParamMap(params));
    }

    private static Tuple2<String, TreeMap<String, Parameter>> fragment(Fragment f, int idx) {
        TreeMap<String, Parameter> empty = TreeMap.empty();
        return f.match(
            str -> Tuple.of(str.s, empty),
            node -> nodeFragment(node, idx),
            rel -> relFragment(rel, idx),
            param -> Tuple.of("$_" + idx, TreeMap.of("_" + idx, param.parameter))
        );
    }

    private static Tuple2<String, TreeMap<String, Parameter>> nodeFragment(Fragment.Node f, int idx) {
        Function<Long, String> propName = propIdx -> concat("_", Long.toString(idx), "_", Long.toString(propIdx));

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
    private static Tuple2<String, TreeMap<String, Parameter>> relFragment(Fragment.Rel f, int idx) {
        Function<Long, String> propName = propIdx -> concat("_", Long.toString(idx), "_", Long.toString(propIdx));

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
