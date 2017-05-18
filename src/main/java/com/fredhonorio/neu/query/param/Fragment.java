package com.fredhonorio.neu.query.param;

import javaslang.collection.Set;
import javaslang.control.Option;
import com.fredhonorio.neu.type.Properties;
import com.fredhonorio.neu.type.Label;

import java.util.function.Function;

public interface Fragment {

    enum Dir {
        TO, FROM, ANY
    }

    <T> T match(Function<Str, T> str, Function<Node, T> node, Function<Rel, T> rel);

    public static class Str implements Fragment {
        public final String s;

        public Str(String s) {
            this.s = s;
        }

        @Override
        public <T> T match(Function<Str, T> str, Function<Node, T> node, Function<Rel, T> rel) {
            return str.apply(this);
        }
    }

    // TODO: nodes can have no name
    public static class Node implements Fragment {
        public final String name;
        public final Set<Label> labels;
        public final Properties properties;

        public Node(String name, Set<Label> labels, Properties properties) {
            this.name = name;
            this.labels = labels;
            this.properties = properties;
        }

        @Override
        public <T> T match(Function<Str, T> str, Function<Node, T> node, Function<Rel, T> rel) {
            return node.apply(this);
        }

        public String pattern() {
            return "(" +
                name +
                labels.toList().map(x -> x.value).prepend("").mkString("",":", " ") +
                properties.pattern(name) +
            ")";
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
        public <T> T match(Function<Str, T> str, Function<Node, T> node, Function<Rel, T> rel) {
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
