package com.fredhonorio.neu.type;

import javaslang.collection.TreeMap;

import java.util.function.Function;

public class NParamMap implements Parameter {
    public final TreeMap<String, Parameter> value;

    public NParamMap(TreeMap<String, Parameter> value) {
        this.value = value;
    }

    public NParamMap put(String key, Parameter value) {
        return new NParamMap(this.value.put(key, value));
    }

    public static NParamMap empty() {
        return new NParamMap(TreeMap.empty());
    }

    @Override
    public <T> T matchParam(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NParamList, T> list, Function<NParamMap, T> map, Function<NString, T> string, Function<NNull, T> nil) {
        return map.apply(this);
    }

    @Override
    public String toString() {
        return "NParamMap(" + Util.printMap(value) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NParamMap nParamMap = (NParamMap) o;

        return value != null ? value.equals(nParamMap.value) : nParamMap.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
