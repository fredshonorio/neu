package com.fredhonorio.neu.type;

import java.util.function.Function;

public class NFloat implements Property, Parameter, Primitive, Result {
    public final double value;

    public NFloat(double value) {
        this.value = value;
    }

    @Override
    public <T> T matchPrim(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NString, T> string, Function<NNull, T> nil) {
        return flt.apply(this);
    }

    @Override
    public <T> T matchResult(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NResultList, T> list, Function<NResultMap, T> map, Function<NString, T> string, Function<NNull, T> nil, Function<NNode, T> node, Function<NPath, T> path, Function<NRelationship, T> relationship) {
        return flt.apply(this);
    }

    @Override
    public <T> T matchProp(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NPropList, T> list, Function<NString, T> string, Function<NNull, T> nil) {
        return flt.apply(this);
    }

    @Override
    public <T> T matchParam(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NParamList, T> list, Function<NParamMap, T> map, Function<NString, T> string, Function<NNull, T> nil) {
        return flt.apply(this);
    }

    @Override
    public String toString() {
        return "NFloat(" + value + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NFloat nFloat = (NFloat) o;

        return Double.compare(nFloat.value, value) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }
}