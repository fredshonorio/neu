package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.Var;
import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.Parameter;
import com.fredhonorio.neu.type.Properties;
import com.fredhonorio.neu.type.Type;
import com.fredhonorio.neu.util.Strings;
import javaslang.Tuple2;
import javaslang.collection.LinkedHashSet;
import javaslang.collection.List;
import javaslang.control.Option;

import java.util.function.Function;

import static com.fredhonorio.neu.util.Strings.concat;

public interface Fragment {

    enum Dir {
        TO, FROM, ANY
    }

    <T> T match(Function<Str, T> str, Function<Node, T> node, Function<Rel, T> rel, Function<Param, T> param);

    public static class Str implements Fragment {
        public final String s;
        public final boolean spaceAfter;

        public Str(String s) {
            this.s = s;
            this.spaceAfter = true;
        }

        public Str(String s, boolean spaceAfter) {
            this.s = s;
            this.spaceAfter = spaceAfter;
        }

        @Override
        public <T> T match(Function<Str, T> str, Function<Node, T> node, Function<Rel, T> rel, Function<Param, T> param) {
            return str.apply(this);
        }
    }

    public static class Node implements Fragment {
        public final Option<Var> name;
        public final com.fredhonorio.neu.type.Node node;

        public Node(Option<Var> name, com.fredhonorio.neu.type.Node node) {
            this.name = name;
            this.node = node;
        }

        @Override
        public <T> T match(Function<Str, T> str, Function<Node, T> node, Function<Rel, T> rel, Function<Param, T> param) {
            return node.apply(this);
        }

        public String pattern(Function<Long, String> propToVarMapping) {
            String middle = Fragment.showNodeOrRelationship(name, false, node.labels.toList().map(l -> l.value), node.properties, propToVarMapping);
            return concat("(", middle, ")");
        }

        public static String propertyPattern(Properties props, Function<Long, String> propToVarMapping) {
            // TODO: properly escape property
            return props.asMap().toList().map(Tuple2::_1)
                .zipWithIndex()
                .map(pair ->
                    pair.transform((propName, propIndex) ->
                        "`" + propName + "`: $" + propToVarMapping.apply(propIndex)))
                .transform(Strings.mkString("{", ",", "}"));
        }

        public static Node fNode(Option<Var> name, LinkedHashSet<Label> labels, Properties properties) {
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
        public final Option<Var> name;
        public final List<Type> type;
        public final Properties properties;

        public Rel(Dir direction, Option<Var> name, List<Type> type, Properties properties) {
            this.direction = direction;
            this.name = name;
            this.type = type;
            this.properties = properties;
        }

        @Override
        public <T> T match(Function<Str, T> str, Function<Node, T> node, Function<Rel, T> rel, Function<Param, T> param) {
            return rel.apply(this);
        }

        public String pattern(Function<Long, String> propToVarMapping) {

            String left = direction == Dir.FROM ? "<-" : "-";
            String right = direction == Dir.TO ? "->" : "-";

            String middle = Fragment.showNodeOrRelationship(name, true, type.map(t -> t.type).toList(), properties, propToVarMapping);

            return !middle.isEmpty()
                ? concat(left, "[", middle, "]", right)
                : concat(left, right);
        }
    }

    // private
    static String showNodeOrRelationship(Option<Var> ref, boolean isRel, List<String> typeOrLabels, Properties properties, Function<Long, String> propToVarMapping) {

        String typeOrLabelSep = isRel ? "|" : ":";

        String typeOrLabelsStr = typeOrLabels.nonEmpty()
                ? concat(":", typeOrLabels.transform(Strings.mkString(typeOrLabelSep)))
                : "";

        String propStr = properties.isEmpty()
            ? ""
            : Node.propertyPattern(properties, propToVarMapping);

        String nameStr = ref.map(r -> r.value).getOrElse("");

        // @formatter:off
        if ((ref.isDefined() || typeOrLabels.nonEmpty()) && properties.nonEmpty()) { // 111
                                                                                     // 101
            return concat(nameStr, typeOrLabelsStr, " ", propStr);                   // 011
        } else if (properties.isEmpty()) {                                           // 110
                                                                                     // 100
                                                                                     // 010
            return concat(nameStr, typeOrLabelsStr);                                 // 000
        } else {                                                                     // 001
            return propStr;
        }
        // @formatter:on
    }

    public static Str str(String s) {
        return new Str(s);
    }

    public static Param param(Parameter param) {
        return new Param(param);
    }
}
