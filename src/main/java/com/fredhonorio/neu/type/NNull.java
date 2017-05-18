package com.fredhonorio.neu.type;

import java.util.function.Function;

public class NNull implements Property, Parameter, Primitive, Result {

    public static final NNull instance = new NNull();

    @Override
    public <T> T matchPrim(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NString, T> string, Function<NNull, T> nil) {
        return nil.apply(this);
    }

    @Override
    public <T> T matchResult(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NResultList, T> list, Function<NResultMap, T> map, Function<NString, T> string, Function<NNull, T> nil, Function<NNode, T> node, Function<NPath, T> path, Function<NRelationship, T> relationship) {
        return nil.apply(this);
    }

    @Override
    public <T> T matchProp(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NPropList, T> list, Function<NString, T> string, Function<NNull, T> nil) {
        return nil.apply(this);
    }

    @Override
    public <T> T matchParam(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NParamList, T> list, Function<NParamMap, T> map, Function<NString, T> string, Function<NNull, T> nil) {
        return nil.apply(this);
    }

    @Override
    public String toString() {
        return "NNull";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }
}
