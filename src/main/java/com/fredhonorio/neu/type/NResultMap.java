package com.fredhonorio.neu.type;

import javaslang.collection.TreeMap;

import java.util.function.Function;

public class NResultMap implements Result {
    public final TreeMap<String, Result> value;

    public NResultMap(TreeMap<String, Result> value) {
        this.value = value;
    }

    public NResultMap put(String key, Result value) {
        return new NResultMap(this.value.put(key, value));
    }

    @Override
    public <T> T matchResult(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NResultList, T> list, Function<NResultMap, T> map, Function<NString, T> string, Function<NNull, T> nil, Function<NNode, T> node, Function<NPath, T> path, Function<NRelationship, T> relationship) {
        return map.apply(this);
    }

    @Override
    public String toString() {
        return "NResultMap(" + Util.printMap(value) + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NResultMap that = (NResultMap) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    public static NResultMap empty = new NResultMap(TreeMap.empty());
}
