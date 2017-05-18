package com.fredhonorio.neu.type;

import java.util.function.Function;

// This value can is a primitive type
public interface Primitive extends Value {
    public <T> T matchPrim(
        Function<NBoolean, T> bool,
        Function<NFloat, T> flt,
        Function<NInteger, T> integer,
        Function<NString, T> string,
        Function<NNull, T> nil
    );

    default Parameter asParam() {
        return matchPrim(
            bool -> bool,
            floating -> floating,
            integer -> integer,
            str -> str,
            nil -> nil
        );
    }
}
