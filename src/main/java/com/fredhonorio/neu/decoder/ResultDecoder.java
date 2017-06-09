package com.fredhonorio.neu.decoder;

import com.fredhonorio.neu.type.*;
import javaslang.Function2;
import javaslang.Function3;
import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.collection.Stream;
import javaslang.control.Either;
import javaslang.control.Option;
import javaslang.control.Try;

import java.util.function.Function;
import java.util.function.Predicate;

public interface ResultDecoder<T> {
    Either<String, T> decode(Result from);

    default Try<T> tryDecode(Result from) {
        return decode(from).fold(msg -> Try.failure(new IllegalArgumentException(msg)), Try::success);
    }

    default <X> ResultDecoder<X> map(Function<T, X> f) {
        return from -> decode(from).map(f);
    }

    default ResultDecoder<T> filter(Predicate<T> pred, Function<T, String> ifMissing) {
        return from -> decode(from).flatMap(v ->
            pred.test(v)
                ? Either.right(v)
                : Either.left(ifMissing.apply(v)));
    }

    default ResultDecoder<T> mapLeft(Function<String, String> f) {
        return from -> decode(from).mapLeft(f);
    }

    default <X> ResultDecoder<X> andThen(Function<T, ResultDecoder<X>> f) {
        return from -> decode(from).flatMap(t -> f.apply(t).decode(from));
    }

    public static ResultDecoder<Result> Value = Either::right;
    public static ResultDecoder<Boolean> Bool = is(Result::asBoolean, "boolean");
    public static ResultDecoder<Double> Float = is(Result::asDouble, "float");
    public static ResultDecoder<Long> Integer = is(Result::asLong, "long");
    public static ResultDecoder<NResultList> List = is(Result::asResultList, "list");
    public static ResultDecoder<NResultMap> Map = is(Result::asResultMap, "map");
    public static ResultDecoder<String> String = is(Result::asString, "string");
    public static ResultDecoder<NNull> Null = is(Result::asNull, "nil");
    public static ResultDecoder<NNode> Node = is(Result::asNode, "node");
    public static ResultDecoder<NPath> Path = is(Result::asPath, "path");
    public static ResultDecoder<NRelationship> Relationship = is(Result::asRelationship, "relationship");

    public static <T> ResultDecoder<List<T>> list(ResultDecoder<T> inner) {
        return List.andThen(list -> ignore ->
            sequence(list.value.toStream().map(inner::decode))
                .mapLeft(s -> "failed decoding element: " + s)
        );
    }

    public static <T> ResultDecoder<Map<String, T>> dict(ResultDecoder<T> inner) {
        return Map
            .andThen(map -> ignore -> {
                    Stream<Tuple2<String, Result>> entries = map.value.toStream();
                    return sequence(entries.map(Tuple2::_2).map(inner::decode))
                        .mapLeft(err -> "failed decoding element: " + err)
                        .map(values -> entries.map(Tuple2::_1).zip(values).toMap(Function.identity()));
                }
            );
    }

    public static <T> ResultDecoder<T> nodeProps(ResultDecoder<T> inner) {
        return Node.andThen(n -> ign -> inner.decode(n.node.properties.asResult()));
    }

    public static <T> ResultDecoder<T> relProps(ResultDecoder<T> inner) {
        return Relationship.andThen(n -> ign -> inner.decode(n.relationship.properties.asResult()));
    }

    @SafeVarargs
    public static <T> ResultDecoder<T> oneOf(ResultDecoder<T>... decoders) {
        return oneOf(javaslang.collection.List.of(decoders));
    }

    public static <T> ResultDecoder<T> field(String key, ResultDecoder<T> inner) {
        return root -> Map.decode(root)
            .flatMap(val -> optionToEither(val.value.get(key), "missing"))
            .flatMap(inner::decode)
            .mapLeft(err -> "field '" + key + "': " + err);
    }

    static <T> ResultDecoder<Option<T>> nullable(ResultDecoder<T> decoder) {
        return oneOf(
            decoder.map(Option::some),
            Null.map(nil -> Option.none())
        );
    }

    static <T> ResultDecoder<T> nullable(ResultDecoder<T> decoder, T defaultValue) {
        return nullable(decoder)
            .map(r -> r.getOrElse(defaultValue));
    }

    static <T> ResultDecoder<T> nullValue(T value) {
        return Null.map(n -> value);
    }

    static <T> ResultDecoder<Option<T>> option(ResultDecoder<T> decoder) {
        return v -> {
            Either<java.lang.String, T> res = decoder.decode(v);
            return Either.right(res.fold(
                err -> Option.none(),
                ok -> Option.some(ok)
            ));
        };
    }

    static ResultDecoder<Result> index(int index) {
        return List
            .andThen(arr -> arr.get(index)
                .map(ResultDecoder::success)
                .getOrElse(failure("no element at index " + index)));
    }

    static <T> ResultDecoder<T> index(int index, ResultDecoder<T> dec) {
        return index(index).andThen(value -> result(dec.decode(value)));
    }

    static <T> ResultDecoder<T> at(List<String> fields, ResultDecoder<T> inner) {
        return fields.foldRight(inner, ResultDecoder::field);
    }

    // TODO: this is cool!
    static <I, T> ResultDecoder<T> enumeration(ResultDecoder<I> inner, Function<I, Option<T>> mapping) {
        return i -> inner.decode(i)
            .flatMap(decoded -> optionToEither(mapping.apply(decoded), "cannot find a matching value for " + decoded));
    }

    static <T extends Enum<T>> ResultDecoder<T> enumByName(Class<T> enumClass, Function<String, String> transform) {
        List<T> enumValues = javaslang.collection.List.of(enumClass.getEnumConstants());
        Function<String, Option<T>> mapping = name -> enumValues.find(e -> transform.apply(e.name()).equals(name));
        return enumeration(String, mapping);
    }

    static <T extends Enum<T>> ResultDecoder<T> enumByName(Class<T> enumClass) {
        return enumByName(enumClass, s -> s);
    }

    static <T> ResultDecoder<T> equal(ResultDecoder<T> decoder, T expected) {
        return decoder.filter(x -> x.equals(expected), actual -> "expected value: '" + expected + "', got '" + actual + "'");
    }

    static <T> ResultDecoder<T> oneOf(List<ResultDecoder<T>> decoders) {
        return val -> {
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

    static <T> Either<String, List<T>> sequence(Seq<Either<String, T>> seq) {

        Option<Either<java.lang.String, T>> failure = seq.find(Either::isRight);

        if (failure.isDefined())
            return Either.left(failure.get().getLeft());

        return Either.right(seq.map(Either::get).toList());
    }

    static <T> ResultDecoder<T> result(Either<String, T> res) {
        return ignore -> res;
    }

    static <T> ResultDecoder<T> success(T res) {
        return ignore -> Either.right(res);
    }

    static <T> ResultDecoder<T> failure(String res) {
        return ignore -> Either.left(res);
    }

    static <A, B, TT> ResultDecoder<TT> map2(ResultDecoder<A> a, ResultDecoder<B> b, Function2<A, B, TT> f) {
        return a.andThen(_a -> b.map(_b -> f.apply(_a, _b)));
    }

    static <A, B, C, TT> ResultDecoder<TT> map3(ResultDecoder<A> a, ResultDecoder<B> b, ResultDecoder<C> c, Function3<A, B, C, TT> f) {
        return a.andThen(_a -> b.andThen(_b -> c.map(_c -> f.apply(_a, _b, _c))));
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
