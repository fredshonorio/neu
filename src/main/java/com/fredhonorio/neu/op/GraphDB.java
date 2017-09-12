package com.fredhonorio.neu.op;

import javaslang.collection.List;
import javaslang.control.Try;
import org.neo4j.driver.v1.StatementRunner;

import java.util.LinkedList;
import java.util.function.Function;

/**
 * A computation in Neo4J
 *
 * @param <T>
 */
public interface GraphDB<T> {

    abstract T run(StatementRunner tx) throws Exception;

    default <U> GraphDB<U> map(Function<T, U> f) {
        return tx -> f.apply(run(tx));
    }

    default <U> GraphDB<U> mapTry(Function<T, Try<U>> f) {
        return tx -> f.apply(run(tx)).get();
    }

    // this is safe because of the submit
    default <U> GraphDB<U> mapTryChecked(Try.CheckedFunction<T, U> f) {
        return tx -> Try.of(() -> f.apply(run(tx))).get();
    }

    default <U> GraphDB<U> flatMap(Function<T, GraphDB<U>> f) {
        return tx -> f.apply(run(tx)).run(tx);
    }

    default <U> GraphDB<U> flatMapTry(Function<T, Try<GraphDB<U>>> f) {
        return tx -> f.apply(run(tx)).get().run(tx);
    }

    default <U> GraphDB<U> flatMapTryChecked(Try.CheckedFunction<T, GraphDB<U>> f) {
        return tx -> Try.of(() -> f.apply(run(tx))).get().run(tx);
    }

    static <T> GraphDB<T> just(T v) { // unit? point? pure?
        return tx -> v;
    }

    static <T> GraphDB<List<T>> sequence(Iterable<GraphDB<T>> ops) {
        return tx -> {
            LinkedList<T> l = new LinkedList<T>();
            for (GraphDB<T> op : ops) {
                l.add(op.run(tx));
            }
            return List.ofAll(l);
        };
    }

    @SafeVarargs
    static <T> GraphDB<List<T>> sequence(GraphDB<T>... ops) {
        return sequence(List.of(ops));
    }
}
