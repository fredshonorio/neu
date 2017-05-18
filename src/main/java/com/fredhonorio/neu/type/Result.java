package com.fredhonorio.neu.type;

import javaslang.control.Option;

import java.util.function.Function;

import static javaslang.control.Option.some;

// This value can be returned in a result
public interface Result extends Value {
    public <T> T matchResult(
        Function<NBoolean, T> bool,
        Function<NFloat, T> flt,
        Function<NInteger, T> integer,
        Function<NResultList, T> list,
        Function<NResultMap, T> map,
        Function<NString, T> string,
        Function<NNull, T> nil,
        Function<NNode, T> node,
        Function<NPath, T> path,
        Function<NRelationship, T> relationship
    );

    default String type() {
        return matchResult(
            bool -> "boolean",
            flt -> "float",
            integer -> "integer",
            list -> "list",
            map -> "map",
            string -> "string",
            nil -> "null",
            node -> "node",
            path -> "path",
            rel -> "relationship"
        );
    }

    default Option<Boolean> asBoolean() {
        return matchResult(bool -> some(bool.value), never(), never(), never(), never(), never(), never(), never(), never(), never());
    }

    default Option<Double> asDouble() {
        return matchResult(never(), flt -> some(flt.value), never(), never(), never(), never(), never(), never(), never(), never());
    }

    default Option<Long> asLong() {
        return matchResult(never(), never(), integer -> some(integer.value), never(), never(), never(), never(), never(), never(), never());
    }

    default Option<NResultList> asResultList() {
        return matchResult(never(), never(), never(), Option::some, never(), never(), never(), never(), never(), never());
    }

    default Option<NResultMap> asResultMap() {
        return matchResult(never(), never(), never(), never(), Option::some, never(), never(), never(), never(), never());
    }

    default Option<String> asString() {
        return matchResult(never(), never(), never(), never(), never(), str -> some(str.value), never(), never(), never(), never());
    }

    default Option<NNull> asNull() {
        return matchResult(never(), never(), never(), never(), never(), never(), Option::some, never(), never(), never());
    }

    default Option<NNode> asNode() {
        return matchResult(never(), never(), never(), never(), never(), never(), never(), Option::some, never(), never());
    }

    default Option<NPath> asPath() {
        return matchResult(never(), never(), never(), never(), never(), never(), never(), never(), Option::some, never());
    }

    default Option<NRelationship> asRelationship() {
        return matchResult(never(), never(), never(), never(), never(), never(), never(), never(), never(), Option::some);
    }

    static <T, X> Function<T, Option<X>> never() {
        return t -> Option.none();
    }
}
