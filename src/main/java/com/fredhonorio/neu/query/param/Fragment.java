package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.type.*;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.LinkedHashSet;
import javaslang.collection.Map;
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

        @Deprecated
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

        public String pattern(Function<String, String> propToVarMapping) {
            // sanitize var string

            String nameStr = name.getOrElse("");

            String labelsStr = node.labels.isEmpty()
                ? ""
                : node.labels.toList().map(x -> x.value).mkString(":", ":", "");

            Properties properties = node.properties;
            String propsStr = properties.isEmpty()
                ? ""
                : propertyPattern(properties, propToVarMapping);

            String space = labelsStr.isEmpty() && nameStr.isEmpty() ? "" : " ";

            return "(" + nameStr + labelsStr + space + propsStr + ")";
        }

        public static String propertyPattern(Properties props, Function<String, String> propToVarMapping) {
            return props.asMap().toList().map(Tuple2::_1).map(prop -> "`" + prop + "`: $" + propToVarMapping.apply(prop)).mkString("{", ",", "}");
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
