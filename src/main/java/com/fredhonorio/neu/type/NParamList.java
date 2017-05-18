package com.fredhonorio.neu.type;

import javaslang.collection.List;

import java.util.function.Function;

public class NParamList implements Parameter {
    public final List<Parameter> value;

    public NParamList(List<Parameter> value) {
        this.value = value;
    }

    @Override
    public <T> T matchParam(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NParamList, T> list, Function<NParamMap, T> map, Function<NString, T> string, Function<NNull, T> nil) {
        return list.apply(this);
    }

    @Override
    public String toString() {
        return "NParamList(" + Util.printList(value) + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NParamList that = (NParamList) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
