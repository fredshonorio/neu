package com.fredhonorio.neu.type;

import java.util.function.Function;

public class NNode implements Result {

    public final long id;
    public final Node node;

    public NNode(long id, Node node) {
        this.id = id;
        this.node = node;
    }

    @Override
    public <T> T matchResult(Function<NBoolean, T> bool, Function<NFloat, T> flt, Function<NInteger, T> integer, Function<NResultList, T> list, Function<NResultMap, T> map, Function<NString, T> string, Function<NNull, T> nil, Function<NNode, T> node, Function<NPath, T> path, Function<NRelationship, T> relationship) {
        return node.apply(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NNode nNode = (NNode) o;

        if (id != nNode.id) return false;
        return node != null ? node.equals(nNode.node) : nNode.node == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (node != null ? node.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NNode(#" + id + ", " + node.describe() + ')';
    }
}
