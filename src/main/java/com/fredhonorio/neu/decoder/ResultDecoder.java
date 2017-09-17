package com.fredhonorio.neu.decoder;

import com.fredhonorio.neu.type.*;
import javaslang.*;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.collection.Stream;
import javaslang.control.Either;
import javaslang.control.Option;
import javaslang.control.Try;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Primitive decoders are named after the java type of the value, specifically, the decoder for the cypher type Integer
 * is named Long because it returns a long, thus avoiding confusion.
 * The wrappers for the cypher types are consistent with cypher terminology, and so the type for Integer is NInteger.
 * This is hopefully ok because it's not likely that the wrappers are used directly
 *
 * @param <T>
 */
@SuppressWarnings("unused")
public interface ResultDecoder<T> {
    Either<String, T> decode(Result from);

    /**
     * Apply this decoder but wrap the result in a {@link Try}
     *
     * @param from The value to decode.
     * @return The result of the decoding.
     */
    default Try<T> tryDecode(Result from) {
        return decode(from).fold(msg -> Try.failure(new IllegalArgumentException(msg)), Try::success);
    }

    /**
     * Apply a function (ideally pure) to the result of this decoder.
     *
     * @param f   The function.
     * @param <X> The new return type for the decoded value.
     * @return The new decoder.
     */
    default <X> ResultDecoder<X> map(Function<T, X> f) {
        return from -> decode(from).map(f);
    }

    /**
     * Apply a function to the result of the decoder. If the function fails (throws an exception), the decoder will fail.
     *
     * @param f   The function.
     * @param <X> The new return type for the decoded value.
     * @return The new decoder.
     */
    default <X> ResultDecoder<X> mapTry(Try.CheckedFunction<T, X> f) {
        return mapTry(f, (x, t) -> t.getMessage());
    }

    /**
     * Apply a function to the result of the decoder. If the function fails (throws an exception), the decoder will fail.
     *
     * @param f   The function.
     * @param msg A function to produce an error message, in case  <code>f</code> fails.
     * @param <X> The new return type for the decoded value.
     * @return The new decoder.
     */
    default <X> ResultDecoder<X> mapTry(Try.CheckedFunction<T, X> f, Function2<T, Throwable, String> msg) {
        return from -> decode(from)
            .flatMap(t ->
                Try.of(() -> f.apply(t))
                    .map(Either::<String, X>right)
                    .getOrElseGet(thr -> Either.left(msg.apply(t, thr))));
    }

    /**
     * Filter the result of this decoder, if the predicate is false the decoder fails. <code>ifMissing</code> is used to
     * produce an error message.
     *
     * @param pred      The predicate.
     * @param ifMissing A function to produce an error message, in case  <code>f</code> fails.
     * @return The new decoder.
     */
    default ResultDecoder<T> filter(Predicate<T> pred, Function<T, String> ifMissing) {
        return from -> decode(from).flatMap(v ->
            pred.test(v)
                ? Either.right(v)
                : Either.left(ifMissing.apply(v)));
    }

    /**
     * Transform the error message, if this decoder has failed.
     *
     * @param f A function to modify the error message.
     * @return The new decoder.
     */
    default ResultDecoder<T> mapLeft(Function<String, String> f) {
        return from -> decode(from).mapLeft(f);
    }

    /**
     * Define a decoder to apply after this one, depending on the result of this one.
     * Notice that the original value is the same, both this decoder and the one returned by <code>f</code> receive the
     * same value (a {@link Result}).
     *
     * @param f   The function to define the next decoder.
     * @param <X> The new return type.
     * @return The new decoder.
     */
    default <X> ResultDecoder<X> andThen(Function<T, ResultDecoder<X>> f) {
        return from -> decode(from).flatMap(t -> f.apply(t).decode(from));
    }

    /**
     * Returns a {@link Result}. Always succeeds.
     */
    ResultDecoder<Result> Value = Either::right;
    /**
     * Decodes a {@link Boolean} from a Cypher boolean.
     */
    ResultDecoder<Boolean> Bool = is(Result::asBoolean, "boolean");

    /**
     * Decodes a {@link Double} from a Cypher float.
     */
    ResultDecoder<Double> Double = is(Result::asDouble, "float");
    /**
     * Decodes a {@link Float} from a Cypher float.
     */
    ResultDecoder<Float> Float = Double.mapTry(java.lang.Double::floatValue);
    /**
     * Decodes a {@link Long} from a Cypher integer.
     */
    ResultDecoder<Long> Long = is(Result::asLong, "long");

    /**
     * Decodes an {@link Integer} from a Cypher integer.
     */
    ResultDecoder<Integer> Integer = Long.mapTry(java.lang.Long::intValue);

    /**
     * Decodes a {@link NResultList} from a Cypher list.
     */
    ResultDecoder<NResultList> List = is(Result::asResultList, "list");
    /**
     * Decodes a {@link NResultMap} from a Cypher map.
     */
    ResultDecoder<NResultMap> Map = is(Result::asResultMap, "map");
    /**
     * Decodes a {@link String} from a Cypher string.
     */
    ResultDecoder<String> String = is(Result::asString, "string");
    /**
     * Decodes a {@link NNull} from a Cypher null.
     */
    ResultDecoder<NNull> Null = is(Result::asNull, "nil");
    /**
     * Decodes a {@link NNode} from a Cypher node.
     */
    ResultDecoder<NNode> Node = is(Result::asNode, "node");
    /**
     * Decodes a {@link NPath} from a Cypher path.
     */
    ResultDecoder<NPath> Path = is(Result::asPath, "path");
    /**
     * Decodes a {@link NRelationship} from a Cypher relationship.
     */
    ResultDecoder<NRelationship> Relationship = is(Result::asRelationship, "relationship");

    /**
     * Decodes a list, applies a given decoder for each value in the list.
     *
     * @param inner The decoder for the value in the list.
     * @param <T>   The type of the value in the list.
     * @return A new decoder.
     */
    static <T> ResultDecoder<List<T>> list(ResultDecoder<T> inner) {
        return List.andThen(list -> ignore ->
            sequence(list.value.toStream().map(inner::decode))
                .mapLeft(s -> "failed decoding element: " + s)
        );
    }

    /**
     * Decodes a map and applies the given decoder for the values. They keys are {@link String}s.
     *
     * @param inner The decoder for the values.
     * @param <T>   The type of the values.
     * @return A new decoder.
     */
    static <T> ResultDecoder<Map<String, T>> dict(ResultDecoder<T> inner) {
        return Map
            .andThen(map -> ignore -> {
                    Stream<Tuple2<String, Result>> entries = map.value.toStream();
                    return sequence(entries.map(Tuple2::_2).map(inner::decode))
                        .mapLeft(err -> "failed decoding element: " + err)
                        .map(values -> entries.map(Tuple2::_1).zip(values).toMap(Function.identity()));
                }
            );
    }

    /**
     * Decodes a node, converts its properties to a map, and applies the given decoder to said map. Would be used with
     * <code>field</code> or <code>dict</code> and other combinators that work on {@link NResultMap}.
     *
     * @param inner The decoder for the node properties.
     * @param <T>   The type of the value taken from the node properties.
     * @return A new decoder.
     */
    static <T> ResultDecoder<T> nodeProps(ResultDecoder<T> inner) {
        return Node.andThen(n -> ign -> inner.decode(n.node.properties.asResult()));
    }

    /**
     * Decodes a relationship, converts its properties to a map, and applies the given decoder to said map. Would be used with
     * <code>field</code> or <code>dict</code> and other combinators that work on {@link NResultMap}.
     *
     * @param inner The decoder for the relationship properties.
     * @param <T>   The type of the value taken from the relationship properties.
     * @return A new decoder.
     */
    static <T> ResultDecoder<T> relProps(ResultDecoder<T> inner) {
        return Relationship.andThen(n -> ign -> inner.decode(n.relationship.properties.asResult()));
    }

    /**
     * Attempts each decoder until one succeeds, if none succeed, this decoder fails.
     *
     * @param decoders A list of decoders
     * @param <T>      The type of the decoded value.
     * @return A new decoder.
     */
    static <T> ResultDecoder<T> oneOf(List<ResultDecoder<T>> decoders) {
        return val -> {
            // application is lazy so that we avoid using more than one successful decoder
            Stream<Either<java.lang.String, T>> results = decoders
                .toStream()
                .map(d -> d.decode(val));

            if (results.isEmpty())
                return Either.left("no decoders given");

            return results
                .find(Either::isRight)
                .getOrElse(() -> Either.left(
                    results
                        .map(Either::getLeft)
                        .prepend("Attempted multiple decoders, all failed:")
                        .mkString("\n\t - ")));
        };
    }

    /**
     * Attempts each decoder until one succeeds, if none succeed, this decoder fails.
     *
     * @param decoders A list of decoders
     * @param <T>      The type of the decoded value.
     * @return A new decoder.
     */
    @SafeVarargs
    static <T> ResultDecoder<T> oneOf(ResultDecoder<T>... decoders) {
        return oneOf(javaslang.collection.List.of(decoders));
    }

    /**
     * Decodes a map, reads a field and applies the given decoder. If the value is not a map, does not have the given
     * key, or the inner decoder fails, then the resulting decoder fails.
     *
     * @param key   The key of the value to decode
     * @param inner The decoder to apply
     * @param <T>   The type of the decoded value
     * @return A new decoder.
     */
    static <T> ResultDecoder<T> field(String key, ResultDecoder<T> inner) {
        return root -> Map.decode(root)
            .flatMap(val -> optionToEither(val.value.get(key), "missing"))
            .flatMap(inner::decode)
            .mapLeft(err -> "field '" + key + "': " + err);
    }

    /**
     * Decodes a map, reads a field and applies the given decoder, wrapped in an {@link Option}. If the value is not
     * a map, or the inner decoder fails, then the resulting decoder fails. If the key does not exist, the decoder
     * succeeds with a {@link Option.None}.
     *
     * @param key   The key of the value to decode.
     * @param inner The decoder to apply.
     * @param <T>   The type of the decoded value.
     * @return A new decoder.
     */
    static <T> ResultDecoder<Option<T>> optionalField(String key, ResultDecoder<T> inner) {
        return root -> Map.decode(root)
            .flatMap(m -> m.value.get(key)
                .map(e -> inner.decode(e).map(Option::some))
                .getOrElse(Either.right(Option.none())))
            .mapLeft(err -> "field '" + key + "': " + err);
    }

    /**
     * Decodes a value that can by a Cypher null.
     *
     * @param decoder The decoder to use if the value is not null.
     * @param <T>     The type of the decoded value.
     * @return A new decoder.
     */
    static <T> ResultDecoder<Option<T>> nullable(ResultDecoder<T> decoder) {
        return oneOf(
            decoder.map(Option::some),
            Null.map(nil -> Option.none())
        );
    }

    /**
     * Decodes a value that can by a Cypher null, in which case a default value is returned.
     *
     * @param decoder      The decoder to use if the value is not null.
     * @param defaultValue The default value.
     * @param <T>          The type of the decoded value.
     * @return A new decoder.
     */
    static <T> ResultDecoder<T> nullable(ResultDecoder<T> decoder, T defaultValue) {
        return nullable(decoder)
            .map(r -> r.getOrElse(defaultValue));
    }

    /**
     * Decodes a Cypher null and returns a given value.
     *
     * @param value The value to return.
     * @param <T>   The type of the value to reeturn-
     * @return A new decoder.
     */
    static <T> ResultDecoder<T> nullValue(T value) {
        return Null.map(n -> value);
    }

    /**
     * Converts a given decoder into one that can fail. If it does, the returned value is a {@link javaslang.control.Option.None}.
     *
     * @param decoder The decoder.
     * @param <T>     The type of the decoded value.
     * @return A new decoder.
     */
    static <T> ResultDecoder<Option<T>> option(ResultDecoder<T> decoder) {
        return v -> {
            Either<java.lang.String, T> res = decoder.decode(v);
            return Either.right(res.fold(
                err -> Option.none(),
                Option::some
            ));
        };
    }

    /**
     * Decodes a list and takes the value at a given index. Fails if the value is not a list or the index is out of
     * bounds.
     *
     * @param index The index of the value to get
     * @return A new decoder.
     */
    static ResultDecoder<Result> index(int index) {
        return List
            .andThen(arr -> arr.get(index)
                .map(ResultDecoder::success)
                .getOrElse(failure("no element at index " + index)));
    }

    /**
     * Decodes a list, takes the value at a given index and applies a given decoder. Fails if the value is not a list
     * or the index is out of bounds.
     *
     * @param index The index of the value to get.
     * @param dec   The decoder for the indexed value.
     * @param <T>   The type of the value.
     * @return A new decoder.
     */
    static <T> ResultDecoder<T> index(int index, ResultDecoder<T> dec) {
        return index(index).andThen(value -> result(dec.decode(value)));
    }

    /**
     * Traverses an object tree using the given list of keys, and finally applies a decoder to the final value.
     *
     * @param fields The sequence of fields to access.
     * @param inner  A decoder for the last value.
     * @param <T>    The type of the decoded value.
     * @return A new decoder.
     */
    static <T> ResultDecoder<T> at(List<String> fields, ResultDecoder<T> inner) {
        return fields.foldRight(inner, ResultDecoder::field);
    }

    static <I, T> ResultDecoder<T> enumeration(ResultDecoder<I> inner, Function<I, Option<T>> mapping) {
        return i -> inner.decode(i)
            .flatMap(decoded -> optionToEither(mapping.apply(decoded), "cannot find a matching value for " + decoded));
    }

    /**
     * Decodes a Cypher string into an enum. <code>transform</code> is used to modify the string value before it is
     * matched against the enum constants.
     *
     * @param enumClass The enum
     * @param transform A function to modify the string value.
     * @param <T>       The enum type.
     * @return A new decoder.
     */
    static <T extends Enum<T>> ResultDecoder<T> enumByName(Class<T> enumClass, Function<String, String> transform) {
        List<T> enumValues = javaslang.collection.List.of(enumClass.getEnumConstants());
        Function<String, Option<T>> mapping = name -> enumValues.find(e -> transform.apply(e.name()).equals(name));
        return enumeration(String, mapping);
    }

    /**
     * Decodes a Cypher string into an enum.
     *
     * @param enumClass The enum
     * @param <T>       The enum type.
     * @return A new decoder.
     */
    static <T extends Enum<T>> ResultDecoder<T> enumByName(Class<T> enumClass) {
        return enumByName(enumClass, s -> s);
    }

    /**
     * Decodes a value only if it is equal to an expected value.
     *
     * @param decoder  The decoder.
     * @param expected The expected value.
     * @param <T>      The type of the decoded value.
     * @return A new decoder.
     */
    static <T> ResultDecoder<T> equal(ResultDecoder<T> decoder, T expected) {
        return decoder.filter(x -> x.equals(expected), actual -> "expected value: '" + expected + "', got '" + actual + "'");
    }

    static <T> Either<String, List<T>> sequence(Seq<Either<String, T>> seq) {

        Option<Either<java.lang.String, T>> failure = seq.find(Either::isLeft);

        if (failure.isDefined())
            return Either.left(failure.get().getLeft());

        return Either.right(seq.map(Either::get).toList());
    }

    /**
     * A decoder that always returns a given result.
     *
     * @param res The result.
     * @param <T> The type of the value.
     * @return A new decoder.
     */
    static <T> ResultDecoder<T> result(Either<String, T> res) {
        return ignore -> res;
    }

    /**
     * A decoder that always succeeds with a given value.
     *
     * @param res The value.
     * @param <T> The type of the value.
     * @return A new decoder.
     */
    static <T> ResultDecoder<T> success(T res) {
        return ignore -> Either.right(res);
    }

    /**
     * A decoder that always fails with a given message.
     *
     * @param res The error message.
     * @param <T> The type of the value.
     * @return A new decoder.
     */
    static <T> ResultDecoder<T> failure(String res) {
        return ignore -> Either.left(res);
    }

    /**
     * A decoder that combines the results of two other decoders.
     *
     * @param a      The first decoder.
     * @param b      The second decoder.
     * @param mapper A function to combine two results.
     * @param <A>    The type of the first decoder.
     * @param <B>    The type of the second decoder.
     * @param <TT>   The type of the combined value.
     * @return A new decoder.
     */
    static <A, B, TT> ResultDecoder<TT> map2(ResultDecoder<A> a, ResultDecoder<B> b, Function2<A, B, TT> mapper) {
        return a.andThen(_a -> b.map(_b -> mapper.apply(_a, _b)));
    }

    /**
     * A decoder that combines the results of three other decoders.
     *
     * @param a      The first decoder.
     * @param b      The second decoder.
     * @param c      The third decoder.
     * @param mapper A function to combine three results.
     * @param <A>    The type of the first decoder.
     * @param <B>    The type of the second decoder.
     * @param <C>    The type of the third decoder.
     * @param <TT>   The type of the combined value.
     * @return A new decoder.
     */
    static <A, B, C, TT> ResultDecoder<TT> map3(ResultDecoder<A> a, ResultDecoder<B> b, ResultDecoder<C> c, Function3<A, B, C, TT> mapper) {
        return a.andThen(_a -> b.andThen(_b -> c.map(_c -> mapper.apply(_a, _b, _c))));
    }

    /**
     * A decoder that combines the results of four other decoders.
     *
     * @param a      The first decoder.
     * @param b      The second decoder.
     * @param c      The third decoder.
     * @param d      The fourth decoder.
     * @param mapper A function to combine four results.
     * @param <A>    The type of the first decoder.
     * @param <B>    The type of the second decoder.
     * @param <C>    The type of the third decoder.
     * @param <D>    The type of the fourth decoder.
     * @param <TT>   The type of the combined value.
     * @return A new decoder.
     */
    static <A, B, C, D, TT> ResultDecoder<TT> map4(ResultDecoder<A> a, ResultDecoder<B> b, ResultDecoder<C> c, ResultDecoder<D> d, Function4<A, B, C, D, TT> mapper) {
        return a.andThen(_a -> b.andThen(_b -> c.andThen(_c -> d.map(_d -> mapper.apply(_a, _b, _c, _d)))));
    }

    /**
     * A decoder that combines the results of five other decoders.
     *
     * @param a      The first decoder.
     * @param b      The second decoder.
     * @param c      The third decoder.
     * @param d      The fourth decoder.
     * @param e      The fifth decoder.
     * @param mapper A function to combine five results.
     * @param <A>    The type of the first decoder.
     * @param <B>    The type of the second decoder.
     * @param <C>    The type of the third decoder.
     * @param <D>    The type of the fourth decoder.
     * @param <E>    The type of the fifth decoder.
     * @param <TT>   The type of the combined value.
     * @return A new decoder.
     */
    static <A, B, C, D, E, TT> ResultDecoder<TT> map5(ResultDecoder<A> a, ResultDecoder<B> b, ResultDecoder<C> c, ResultDecoder<D> d, ResultDecoder<E> e, Function5<A, B, C, D, E, TT> mapper) {
        return a.andThen(_a -> b.andThen(_b -> c.andThen(_c -> d.andThen(_d -> e.map(_e -> mapper.apply(_a, _b, _c, _d, _e))))));
    }

    /**
     * A decoder that combines the results of six other decoders.
     *
     * @param a      The first decoder.
     * @param b      The second decoder.
     * @param c      The third decoder.
     * @param d      The fourth decoder.
     * @param e      The fifth decoder.
     * @param f      The sixth decoder.
     * @param mapper A function to combine six results.
     * @param <A>    The type of the first decoder.
     * @param <B>    The type of the second decoder.
     * @param <C>    The type of the third decoder.
     * @param <D>    The type of the fourth decoder.
     * @param <E>    The type of the fifth decoder.
     * @param <F>    The type of the sixth decoder.
     * @param <TT>   The type of the combined value.
     * @return A new decoder.
     */
    static <A, B, C, D, E, F, TT> ResultDecoder<TT> map6(ResultDecoder<A> a, ResultDecoder<B> b, ResultDecoder<C> c, ResultDecoder<D> d, ResultDecoder<E> e, ResultDecoder<F> f, Function6<A, B, C, D, E, F, TT> mapper) {
        return a.andThen(_a -> b.andThen(_b -> c.andThen(_c -> d.andThen(_d -> e.andThen(_e -> f.map(_f -> mapper.apply(_a, _b, _c, _d, _e, _f)))))));
    }

    /**
     * A decoder that combines the results of seven other decoders.
     *
     * @param a      The first decoder.
     * @param b      The second decoder.
     * @param c      The third decoder.
     * @param d      The fourth decoder.
     * @param e      The fifth decoder.
     * @param f      The sixth decoder.
     * @param g      The seventh decoder.
     * @param mapper A function to combine seven results.
     * @param <A>    The type of the first decoder.
     * @param <B>    The type of the second decoder.
     * @param <C>    The type of the third decoder.
     * @param <D>    The type of the fourth decoder.
     * @param <E>    The type of the fifth decoder.
     * @param <F>    The type of the sixth decoder.
     * @param <G>    The type of the seventh decoder.
     * @param <TT>   The type of the combined value.
     * @return A new decoder.
     */
    static <A, B, C, D, E, F, G, TT> ResultDecoder<TT> map7(ResultDecoder<A> a, ResultDecoder<B> b, ResultDecoder<C> c, ResultDecoder<D> d, ResultDecoder<E> e, ResultDecoder<F> f, ResultDecoder<G> g, Function7<A, B, C, D, E, F, G, TT> mapper) {
        return a.andThen(_a -> b.andThen(_b -> c.andThen(_c -> d.andThen(_d -> e.andThen(_e -> f.andThen(_f -> g.map(_g -> mapper.apply(_a, _b, _c, _d, _e, _f, _g))))))));
    }

    /**
     * A decoder that combines the results of eight other decoders.
     *
     * @param a      The first decoder.
     * @param b      The second decoder.
     * @param c      The third decoder.
     * @param d      The fourth decoder.
     * @param e      The fifth decoder.
     * @param f      The sixth decoder.
     * @param g      The seventh decoder.
     * @param h      The eighth decoder.
     * @param mapper A function to combine eight results.
     * @param <A>    The type of the first decoder.
     * @param <B>    The type of the second decoder.
     * @param <C>    The type of the third decoder.
     * @param <D>    The type of the fourth decoder.
     * @param <E>    The type of the fifth decoder.
     * @param <F>    The type of the sixth decoder.
     * @param <G>    The type of the seventh decoder.
     * @param <H>    The type of the eighth decoder.
     * @param <TT>   The type of the combined value.
     * @return A new decoder.
     */
    static <A, B, C, D, E, F, G, H, TT> ResultDecoder<TT> map8(ResultDecoder<A> a, ResultDecoder<B> b, ResultDecoder<C> c, ResultDecoder<D> d, ResultDecoder<E> e, ResultDecoder<F> f, ResultDecoder<G> g, ResultDecoder<H> h, Function8<A, B, C, D, E, F, G, H, TT> mapper) {
        return a.andThen(_a -> b.andThen(_b -> c.andThen(_c -> d.andThen(_d -> e.andThen(_e -> f.andThen(_f -> g.andThen(_g -> h.map(_h -> mapper.apply(_a, _b, _c, _d, _e, _f, _g, _h)))))))));
    }

    // private
    static <T> ResultDecoder<T> is(Function<Result, Option<T>> extract, String typeName) {
        return r -> extract.apply(r)
            .map(Either::<String, T>right)
            .getOrElse(Either.left("Expected " + typeName + " got " + r.type()));
    }

    static <L, R> Either<L, R> optionToEither(Option<R> r, L l) {
        return r.map(Either::<L, R>right)
            .getOrElse(Either.left(l));
    }
}
