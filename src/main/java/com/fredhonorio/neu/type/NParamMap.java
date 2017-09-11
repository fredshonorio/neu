package com.fredhonorio.neu.type;

import javaslang.Tuple2;
import javaslang.collection.TreeMap;
import javaslang.control.Option;

import java.util.function.Function;

public class NParamMap implements Parameter {
    public final TreeMap<String, Parameter> value;

    public NParamMap(TreeMap<String, Parameter> value) {
        this.value = value;
    }

    public NParamMap put(String k, Parameter v) {
        return new NParamMap(value.put(k, v));
    }

    public NParamMap put(String k, boolean v) {
        return new NParamMap(value.put(k, Value.nBoolean(v)));
    }

    public NParamMap put(String k, String v) {
        return new NParamMap(value.put(k, Value.nString(v)));
    }

    public NParamMap put(String k, double v) {
        return new NParamMap(value.put(k, Value.nFloat(v)));
    }

    public NParamMap put(String k, long v) {
        return new NParamMap(value.put(k, Value.nInteger(v)));
    }

    public NParamMap putIfDefined(String k, Option<? extends Parameter> prop) {
        return prop.map(p -> put(k, p)).getOrElse(this);
    }

    public <T> NParamMap putIfDefined(String k, Option<T> prop, Function<T, Parameter> converter) {
        return prop.map(p -> put(k, converter.apply(p))).getOrElse(this);
    }

    public static NParamMap of(String k, Parameter v) {
        return new NParamMap(TreeMap.of(k, v));
    }

    public static NParamMap of(String k, boolean v) {
        return new NParamMap(TreeMap.of(k, Value.nBoolean(v)));
    }

    public static NParamMap of(String k, String v) {
        return new NParamMap(TreeMap.of(k, Value.nString(v)));
    }

    public static NParamMap of(String k, double v) {
        return new NParamMap(TreeMap.of(k, Value.nFloat(v)));
    }

    public static NParamMap of(String k, long v) {
        return new NParamMap(TreeMap.of(k, Value.nInteger(v)));
    }

    public static NParamMap empty() {
        return new NParamMap(TreeMap.empty());
    }

    public static NParamMap of(Iterable<Tuple2<String, Parameter>> map) {
        return new NParamMap(TreeMap.ofEntries(map));
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
