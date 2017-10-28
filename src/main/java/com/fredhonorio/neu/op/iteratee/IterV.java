package com.fredhonorio.neu.op.iteratee;

import javaslang.collection.List;
import javaslang.collection.Seq;

import java.util.function.Function;

public abstract class IterV<E, A> {

    abstract A run();

    public <T> T match(Function<Cont<E, A>, T> cont, Function<Done<E, A>, T> done) {
        return
            this instanceof Cont ? cont.apply((Cont<E, A>) this) :
            this instanceof Done ? done.apply((Done<E, A>) this) :
            Util.fail("Match error");
    }

    public <B> IterV<E, B> flatMap(Function<A, IterV<E, B>> f) {
        return this.match(
            cont -> new Cont<E, B>(e -> cont.k.apply(e).flatMap(f) ),
            done -> f.apply(done.a)
                .match(
                    cont_ -> cont_.k.apply(done.e),
                    done_ -> new Done<>(done_.a, done.e)
                )
        );
    }

    public static class Cont<E, A> extends IterV<E, A> {
        public final Function<Input<E>, IterV<E, A>> k;

        private Cont(Function<Input<E>, IterV<E, A>> k) {
            this.k = k;
        }

        @Override
        A run() {
            return k.apply(Input.eof()).run();
        }
    }

    public static class Done<E, A> extends IterV<E, A> {
        public final A a;
        public final Input<E> e;

        private Done(A a, Input<E> e) {
            this.a = a;
            this.e = e;
        }

        @Override
        A run() {
            return a;
        }
    }

    boolean isDone() {
        return match(cont -> false, done -> true);
    }

    boolean isCont() {
        return match(cont -> true, done -> false);
    }

    public Cont<E, A> asContUnsafe() {
        return match(c -> c, d -> Util.fail("expected Cont, got Done"));
    }

    public Done<E, A> asDoneUnsafe() {
        return match(c -> Util.fail("expected Done, got Cont"), d -> d);
    }

    public static <E, A> Done<E, A> done(A a, Input<E> e) {
        return new Done<>(a, e);
    }

    public static <E, A> Cont<E, A> cont(Function<Input<E>, IterV<E, A>> k) {
        return new Cont<>(k);
    }
}
