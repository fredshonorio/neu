package com.fredhonorio.neu.type;

import javaslang.Tuple;
import javaslang.collection.*;
import javaslang.test.Arbitrary;
import javaslang.test.Gen;
import org.junit.Test;

import java.util.Random;


public class ParameterTest {

    public static <T> Arbitrary<T> oneOf(Arbitrary<? extends T>... xs) {
        Array<Arbitrary<? extends T>> arr = Array.of(xs);
        return size -> rng -> arr.get(rng.nextInt(arr.size())).apply(size).apply(rng);
    }

    static Gen<String> genKey = Arbitrary.string(Gen.choose('a', 'z')).apply(50);

    static Arbitrary<NBoolean> genBool = Gen.choose(List.of(true, false)).map(Value::nBoolean).arbitrary();

    @Deprecated
    static Arbitrary<NFloat> genFloat = size -> Arbitrary.integer().map(Value::nFloat).apply(10000);
    @Deprecated
    static Arbitrary<NInteger> genInt = size -> Arbitrary.integer().map(Value::nInteger).apply(10000);

    static Arbitrary<NString> genStr = size -> Arbitrary.string(Gen.choose('a', 'z')).map(Value::nString).apply(10);
    static Arbitrary<NNull> genNull = Gen.of(NNull.instance).arbitrary();

    public static <T> Gen<Seq<T>> sequence(Seq<Gen<T>> gens) {
        return rng -> gens.foldLeft(
            List.<T>empty(),
            (z, x) -> z.append(x.apply(rng))
        );
    }

    static Gen<NParamList> genList(int flatSize) {
        return Arbitrary.list(parameters()).map(Value::paramList).apply(flatSize);
    }

    static Gen<NParamMap> genMap(int flatSize) {
        return gen -> {
            int root = gen.nextInt(flatSize);
            int rem = flatSize - root;

            return Arbitrary
                .list(parameters())
                .apply(root)
                .flatMap(params -> sequence(params.map(p -> genKey.map(k -> Tuple.of(k, p)))))
                .map(entries -> new NParamMap(TreeMap.ofEntries(entries)))
                .apply(gen);
        };
    }



    public static Arbitrary<Parameter> parameters() {
        return size -> {
            if (size <= 0) {
                return oneOf(genList(size - 1).arbitrary(), genMap(size - 1).arbitrary(), genBool, genFloat, genInt, genStr, genNull).apply(size);
            } else {
                return Gen.of(new NBoolean(false));
            }
        };
    }

    @Test
    public void v() {

        Stream.range(0, 100)
            .peek(System.out::println)
            .map(i -> parameters().apply(i))
            .forEach(gen ->
                System.out.println(gen.apply(new Random()))
            );
    }

}