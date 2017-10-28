package com.fredhonorio.neu.op.iteratee;

import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.collection.Stream;
import javaslang.control.Option;

import java.util.function.Function;

import static com.fredhonorio.neu.op.iteratee.IterV.cont;
import static com.fredhonorio.neu.op.iteratee.IterV.done;

public class Iteratees {

    // stack safe => ugly
    // it looks like stack safety of the enumerator is sufficient to make the whole thing work ok
    public static <E, A> IterV<E, A> enumerate(Seq<E> list, IterV<E, A> it) {

        Seq<E> _list = list;
        IterV<E, A> _it = it;

        while (true) {
            if (_list.isEmpty())
                return _it;
            else if (_it.isDone())
                return _it;
            else {
                E head = _list.head();
                Seq<E> tail = _list.tail();
                Function<Input<E>, IterV<E, A>> k = _it.asContUnsafe().k;

                _list = tail;
                _it = k.apply(Input.el(head));
            }
        }
    }

    public static <E> IterV<E, List<E>> all() {
        return cont(allStep(List.empty()));
    }

    private static <E> Function<Input<E>, IterV<E, List<E>>> allStep(List<E> acc) {
        return i -> i.match(
            el -> cont(allStep(acc.append(el.e))),
            empty -> cont(allStep(acc)),
            eof -> done(acc, eof)
        );
    }

    public static <E, A> IterV<E, Integer> counter() {
        return cont(counterStep(0));
    }

    private static <E> Function<Input<E>, IterV<E, Integer>> counterStep(final int n) {
        return i -> i.match(
            el -> cont(counterStep(n + 1)),
            empty -> cont(counterStep(n)),
            eof -> done(n, eof)
        );
    }

    public static <E> IterV<E, Void> drop(int n) {
        return n == 0
            ? done(null, Input.empty())
            : cont(in -> dropStep(n, in));
    }

    private static <E> IterV<E, Void> dropStep(int n, Input<E> i) {
        return i.match(
            el -> drop(n - 1),
            empty -> cont(in -> dropStep(n, in)),
            eof -> done(null, eof)
        );
    }

    public static <E> IterV<E, Option<E>> head() {
        return cont(headStep());
    }

    private static <E> Function<Input<E>, IterV<E, Option<E>>> headStep() {
        return i -> i.match(
            el -> done(Option.some(el.e), Input.empty()),
            empty -> cont(headStep()),
            eof -> done(Option.none(), eof)
        );
    }

    public static <E> IterV<E, Option<E>> drop1Keep1() {
        return Iteratees.<E>drop(1).flatMap(__ -> head());
    }

    public static void main(String[] args) {

        Integer x = enumerate(List.range(0, 1000000), counter())
            .run();

        System.out.println(x);

        System.out.println(enumerate(Stream.range(0, 1000000), drop1Keep1()).run());


        // test this with a neo4j query
        // UNWIND range(0, 10000000) as x
        // RETURN x


        List<Integer> xs = enumerate(List.range(0, 1000000), all()).run(); // ends eventually

        System.out.println(xs);
    }


}
