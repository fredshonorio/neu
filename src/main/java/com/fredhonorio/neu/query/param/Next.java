package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.Statement;
import com.fredhonorio.neu.type.*;
import javaslang.collection.LinkedHashSet;
import javaslang.collection.List;
import javaslang.control.Option;

import java.util.TreeMap;

import static com.fredhonorio.neu.query.param.Fragment.Node.fNode;
import static javaslang.collection.LinkedHashSet.empty;
import static javaslang.control.Option.none;

public interface Next {

    interface Str extends Fragments {
        default Builder.StrB s(String s) {
            return new Builder.StrB(fragments().append(new Fragment.Str(s)));
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

        default Builder.NodeB node(String name, Label label) {
            return node(fNode(Option.of(name), LinkedHashSet.of(label), Properties.empty()));
        }

        default Builder.NodeB node(String name, Iterable<Label> labels) {
            return node(fNode(Option.of(name), LinkedHashSet.ofAll(labels), Properties.empty()));
        }

        default Builder.NodeB node(String name, Label label, Properties properties) {
            return node(fNode(Option.of(name), LinkedHashSet.of(label), properties));
        }

        default Builder.NodeB node(String name, Iterable<Label> labels, Properties properties) {
            return node(fNode(Option.of(name), LinkedHashSet.ofAll(labels), properties));
        }
    }

    interface From extends Fragments {
        default Builder.FromB from(String s) {
            return new Builder.FromB(fragments().append(new Fragment.Rel(Fragment.Dir.FROM, Option.some(s), none())));
        }
    }

    interface To extends Fragments {
        default Builder.ToB to(String s) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, Option.some(s), none())));
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

            int indexed = 0;
            String query = "";
            TreeMap<String, Parameter> params = new TreeMap<>();
            for (Fragment fragment : fragments) {

                Option<Parameter> p = fragment.match(
                    s -> Option.<Parameter>none(),
                    node -> Option.<Parameter>when(
                        !node.node.properties.isEmpty(),
                        () -> new NParamMap(node.node.properties.asMap().mapValues(Property::asParam))
                    ), // convert
                    rel -> Option.<Parameter>none(),
                    param -> Option.some(param.parameter)
                );

                int i = indexed;
                String q = fragment.match(
                    str -> str.s,
                    node -> node.pattern("_" + i),
                    rel -> rel.pattern(),
                    param -> "$_" + i
                );

                query += q;

                p.forEach(param -> params.put("_" + i, param));

                indexed += fragment.match(
                    str -> 0,
                    node -> 1,
                    rel -> 0,
                    param -> 1
                );

            }

            // System.out.println(query);
            // params.forEach((k, v) -> System.out.println(k + "=" + v));

            return new Statement(
                query,
                new NParamMap(javaslang.collection.TreeMap.ofAll(params))
            );
        }

        default String show() {
            return null;
        }
    }
}
