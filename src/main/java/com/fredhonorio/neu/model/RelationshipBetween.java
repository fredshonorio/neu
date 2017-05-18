package com.fredhonorio.neu.model;

import com.fredhonorio.neu.type.Node;

public class RelationshipBetween {

    public final Relationship relationship;
    public final Id<Node> from;
    public final Id<Node> to;

    public RelationshipBetween(Relationship relationship, Id<Node> from, Id<Node> to) {
        this.relationship = relationship;
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelationshipBetween that = (RelationshipBetween) o;

        if (relationship != null ? !relationship.equals(that.relationship) : that.relationship != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        return to != null ? to.equals(that.to) : that.to == null;
    }

    @Override
    public int hashCode() {
        int result = relationship != null ? relationship.hashCode() : 0;
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }
}
