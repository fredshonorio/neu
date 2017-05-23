package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.Node;
import com.fredhonorio.neu.type.Parameter;
import com.fredhonorio.neu.type.Properties;
import javaslang.collection.LinkedHashSet;
import javaslang.control.Option;

import java.util.function.Function;

public interface Fragment {

    enum Dir {
        TO, FROM, ANY
    }

    <T> T match(Function<Str, T> str, Function<Node, T> node, Function<Rel, T> rel, Function<Param, T> param);

    public static class Str implements Fragment {
        public final String s;

        public Str(String s) {
            this.s = s;
        }

        @Override
        public <T> T match(Function<Str, T> str, Function<Node, T> node, Function<Rel, T> rel, Function<Param, T> param) {
            return str.apply(this);
        }
    }

    // TODO: nodes can have no name
    public static class Node implements Fragment {
        public final Option<String> name;
        public final com.fredhonorio.neu.type.Node node;

        public Node(Option<String> name, com.fredhonorio.neu.type.Node node) {
            this.name = name;
            this.node = node;
        }

        @Override
        public <T> T match(Function<Str, T> str, Function<Node, T> node, Function<Rel, T> rel, Function<Param, T> param) {
            return node.apply(this);
        }

        public String pattern(String var) {
            // sanitize var string

            String nm = name.getOrElse("");

            String lbls = node.labels.isEmpty()
                ? ""
                : node.labels.toList().map(x -> x.value).mkString(":", ":", "")
                ;

            String prps = node.properties.isEmpty()
                ? ""
                : !lbls.isEmpty() || !nm.isEmpty()
                    ? " $" + var + ""
                    : "$" + var + "";


            return "(" + nm + lbls + prps + ")";
        }

        public static Node fNode(Option<String> name, LinkedHashSet<Label> labels, Properties properties) {
            return new Node(
                name,
                new com.fredhonorio.neu.type.Node(
                    labels,
                    properties
                )
            );
        }
    }

    public static class Param implements Fragment {
        public final Parameter parameter;

        public Param(Parameter parameter) {
            this.parameter = parameter;
        }

        @Override
        public <T> T match(Function<Str, T> str, Function<Node, T> node, Function<Rel, T> rel, Function<Param, T> param) {
            return param.apply(this);
        }
    }

    // TODO: relations have properties as well
    public static final class Rel implements Fragment {
        public final Dir direction;
        public final Option<String> name;
        public final Option<String> type;

        public Rel(Dir direction, Option<String> name, Option<String> type) {
            this.direction = direction;
            this.name = name;
            this.type = type;
        }

        @Override
        public <T> T match(Function<Str, T> str, Function<Node, T> node, Function<Rel, T> rel, Function<Param, T> param) {
            return rel.apply(this);
        }

        public String pattern() {

            String left = direction == Dir.FROM ? "<-" : "-";
            String right = direction == Dir.TO ? "->" : "-";

            String middle = name.getOrElse("") + type.map(t -> ":" + t).getOrElse("");

            return name.isDefined() || type.isDefined()
                ? left + "[" + middle + "]" + right
                : left + right;
        }
    }
}
