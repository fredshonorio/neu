package com.fredhonorio.neu.op.iteratee;

import java.util.function.Function;

public abstract class Input<E> {

    public <T> T match(Function<El<E>, T> el, Function<Empty<E>, T> empty, Function<EOF<E>, T> eof) {
        return
            this instanceof El ? el.apply((El<E>) this) :
            this instanceof Empty ? empty.apply((Empty<E>) this) :
            this instanceof EOF ? eof.apply((EOF<E>) this) :
            Util.fail("Match error");
    }

    public static final class El<E> extends Input<E> {
        public final E e;

        private El(E e) {
            this.e = e;
        }
    }

    // should this just be an object that extends Input<Void>?
    public static final class Empty<E> extends Input<E> {
        private Empty() {}
    }

    public static final class EOF<E> extends Input<E> {
        private EOF() {}
    }

    public static <E> Empty<E> empty() {
        return new Empty<>();
    }

    public static <E> EOF<E> eof() {
        return new EOF<>();
    }

    public static <E> El<E> el(E e) {
        return new El<>(e);
    }


}
