package com.fredhonorio.neu.query.param;

import javaslang.collection.Set;
import javaslang.control.Option;
import com.fredhonorio.neu.type.Properties;
import com.fredhonorio.neu.type.Label;

public interface Fragment {

    enum Dir {
        TO, FROM, ANY
    }

    public static class Str implements Fragment {
        public final String s;

        public Str(String s) {
            this.s = s;
        }
    }

    public static class Node implements Fragment {
        public final String name; // opt?
        public final Set<Label> labels;
        public final Properties properties;

        public Node(String name, Set<Label> labels, Properties properties) {
            this.name = name;
            this.labels = labels;
            this.properties = properties;
        }
    }

    public static final class Rel implements Fragment {
        public final Dir direction;
        public final Option<String> name;
        public final Option<String> type;

        public Rel(Dir direction, Option<String> name, Option<String> type) {
            this.direction = direction;
            this.name = name;
            this.type = type;
        }
    }
}
