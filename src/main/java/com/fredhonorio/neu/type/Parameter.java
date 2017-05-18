package com.fredhonorio.neu.type;

import java.util.function.Function;

// This value can be passed as a parameter in a statement
public interface Parameter extends Value {
    public <T> T matchParam(
        Function<NBoolean, T> bool,
        Function<NFloat, T> flt,
        Function<NInteger, T> integer,
        Function<NParamList, T> list,
        Function<NParamMap, T> map,
        Function<NString, T> string,
        Function<NNull, T> nil
    );
}
