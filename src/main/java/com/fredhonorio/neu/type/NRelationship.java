package com.fredhonorio.neu.type;

import java.util.function.Function;

public class NRelationship implements Result {

    public final long id;
    public final long startId;
    public final long endId;
    public final Relationship relationship;

    public NRelationship(long id, long startId, long endId, Relationship relationship) {
        this.id = id;
        this.startId = startId;
        this.endId = endId;
        this.relationship = relationship;
    }


    @Override
    public <T> T matchResult(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NResultList, T> list, Function<NResultMap, T> map, Function<NString, T> string, Function<NNull, T> nil, Function<NNode, T> node, Function<NPath, T> path, Function<NRelationship, T> relationship) {
        return relationship.apply(this);
    }

    @Override
    public String toString() {
        return "NRelationship{" +
            "id=" + id +
            ", startId=" + startId +
            ", endId=" + endId +
            ", relationship=" + relationship +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NRelationship that = (NRelationship) o;

        if (id != that.id) return false;
        if (startId != that.startId) return false;
        if (endId != that.endId) return false;
        return relationship != null ? relationship.equals(that.relationship) : that.relationship == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (startId ^ (startId >>> 32));
        result = 31 * result + (int) (endId ^ (endId >>> 32));
        result = 31 * result + (relationship != null ? relationship.hashCode() : 0);
        return result;
    }
}
