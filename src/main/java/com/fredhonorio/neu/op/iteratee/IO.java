package com.fredhonorio.neu.op.iteratee;

import javaslang.collection.Stream;
import javaslang.control.Try;

import java.util.function.Function;

public interface IO<T> {

    Try<T> unsafePerform();

    default <U> IO<U> map(Function<T, U> f) {
        return () -> unsafePerform().map(f);
    }

    default <U> IO<U> flatMap(Function<T, IO<U>> f) {
        return () -> unsafePerform()
            .flatMap(t -> f.apply(t).unsafePerform());
    }

    static <T> IO<Stream<T>> sequence(Iterable<IO<T>> ops) {
        return Stream.ofAll(ops)
            .foldLeft(
                pure(Stream.<T>empty()),
                (z, x) -> z.flatMap(zz -> x.map(zz::append)));
    }

    static <T> IO<T> pure(T value) {
        return () -> Try.success(value);
    }

    static <T> IO<T> fail(Throwable exception) {
        return () -> Try.failure(exception);
    }
}
