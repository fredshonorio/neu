package com.fredhonorio.neu.query;

import com.fredhonorio.neu.model.Path;
import javaslang.collection.List;
import javaslang.control.Option;


/**
 * [MATCH WHERE]
 * [OPTIONAL MATCH WHERE] -- does optional match need a match before?
 * [WITH [ORDER BY] [SKIP] [LIMIT]] -- no with yet
 * (CREATE [UNIQUE] | MERGE)* -- no create yet
 * [SET|DELETE|REMOVE|FOREACH]* -- none of these yet
 * [RETURN [ORDER BY] [SKIP] [LIMIT]]
 */
public class Fragment {

    public static class Match {
        public final boolean optional;
        public final List<Path> paths;
        public final Fragment next;

        public Match(boolean optional, List<Path> paths, Fragment next) {
            this.optional = optional;
            this.paths = paths;
            this.next = next;
        }
    }

    public static class Where {

    }

    public static class OrderBy {
    }

    public static class Skip {
    }

    public static class Limit {
    }

    public static class Return {
        public final List<?> things;
        public final Option<OrderBy> orderBy;
        public final Option<Skip> skip;
        public final Option<Limit> limit;

        public Return(List<?> things, Option<OrderBy> orderBy, Option<Skip> skip, Option<Limit> limit) {
            this.things = things;
            this.orderBy = orderBy;
            this.skip = skip;
            this.limit = limit;
        }
    }
}
