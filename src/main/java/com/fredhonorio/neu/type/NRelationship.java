package com.fredhonorio.neu.type;

import org.neo4j.driver.v1.types.Relationship;

import java.util.function.Function;

public class NRelationship implements Result {

    public final Relationship value;

    public NRelationship(Relationship value) {
        this.value = value;
    }

    @Override
    public <T> T matchResult(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NResultList, T> list, Function<NResultMap, T> map, Function<NString, T> string, Function<NNull, T> nil, Function<NNode, T> node, Function<NPath, T> path, Function<NRelationship, T> relationship) {
        return relationship.apply(this);
    }

    @Override
    public String toString() {
        return "NRelationship{" +
            "value=" + value +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NRelationship that = (NRelationship) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
