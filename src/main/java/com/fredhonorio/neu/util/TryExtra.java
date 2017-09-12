package com.fredhonorio.neu.util;

import javaslang.control.Try;

public class TryExtra {

    public static <T> Try<T> rethrow(Try<T> tr, String message) {
        return tr.isFailure()
            ? Try.failure(new RuntimeException(message, tr.getCause()))
            : tr;
    }

    public static <T> T unsafe(Try.CheckedSupplier<T> t) {
        return Try.of(t).get();
    }
}
