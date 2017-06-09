package com.fredhonorio.neu.type;

import java.util.function.Function;

// This value can be a property of a node
public interface Property extends Value {
    public <T> T matchProp(
        Function<NBoolean, T> bool,
        Function<NFloat, T> flt,
        Function<NInteger, T> integer,
        Function<NPropList, T> list,
        Function<NString, T> string,
        Function<NNull, T> nil
    );

    public static Parameter asParam(Property prop) {
        return prop.matchProp(
            bool -> bool,
            floating -> floating,
            integer -> integer,
            list -> new NParamList(list.value.map(Primitive::asParam)),
            string -> string,
            nil -> nil
        );
    }

    public static Result asResult(Property prop) {
        return prop.matchProp(
            bool -> bool,
            floating -> floating,
            integer -> integer,
            list -> new NResultList(list.value.map(Primitive::asResult)),
            string -> string,
            nil -> nil
        );
    }
}
