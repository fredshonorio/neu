package com.fredhonorio.neu.type;

import javaslang.collection.List;
import javaslang.control.Option;

import java.util.function.Function;

public class NResultList implements Result {
    // is Result too wide?
    public final List<Result> value;

    public NResultList(List<Result> value) {
        this.value = value;
    }

    public Option<Result> get(int i) {
        return i >= 0 && i < value.size()
            ? Option.some(value.get(i))
            : Option.none();
    }

    @Override
    public <T> T matchResult(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NResultList, T> list, Function<NResultMap, T> map, Function<NString, T> string, Function<NNull, T> nil, Function<NNode, T> node, Function<NPath, T> path, Function<NRelationship, T> relationship) {
        return list.apply(this);
    }

    @Override
    public String toString() {
        return "NResultList(" + Util.printList(value) + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NResultList that = (NResultList) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
