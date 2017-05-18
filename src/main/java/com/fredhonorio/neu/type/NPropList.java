package com.fredhonorio.neu.type;

import javaslang.collection.List;

import java.util.function.Function;

public class NPropList implements Property {
    public final List<Primitive> value;

    public NPropList(List<Primitive> value) {
        this.value = value;
    }

    @Override
    public <T> T matchProp(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NPropList, T> list, Function<NString, T> string, Function<NNull, T> nil) {
        return list.apply(this);
    }

    @Override
    public String toString() {
        return "NPropList(" + Util.printList(value) + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NPropList nPropList = (NPropList) o;

        return value != null ? value.equals(nPropList.value) : nPropList.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
