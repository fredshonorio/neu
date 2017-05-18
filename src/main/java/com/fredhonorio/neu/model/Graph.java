package com.fredhonorio.neu.model;

import com.fredhonorio.neu.type.Node;
import javaslang.Tuple2;
import javaslang.collection.Set;
import javaslang.collection.Stream;
import javaslang.collection.TreeMap;
import javaslang.control.Option;

import java.util.function.Predicate;

public class Graph {

    public final long nextId;
    public final TreeMap<Long, Node> nodes;
    public final TreeMap<Long, RelationshipBetween> relationships;

    private final Stream<IdAnd<Node>> nodesById;
    private final Stream<IdAnd<RelationshipBetween>> relationshipsById;

    protected Graph(long nextId, TreeMap<Long, Node> nodes, TreeMap<Long, RelationshipBetween> relationships) {
        this.nextId = nextId;
        this.nodes = nodes;
        this.relationships = relationships;
        nodesById = nodes.iterator().toStream().map(x -> new IdAnd<>(x._1, x._2));
        relationshipsById = relationships.iterator().toStream().map(x -> new IdAnd<>(x._1, x._2));
    }

    public Graph add(Node node) {
        return new Graph(nextId + 1, nodes.put(nextId, node), relationships);
    }

    public Graph update(Id<Node> id, Node node) {
        return new Graph(nextId, nodes.put(id.value, node), relationships);
    }

    public Graph merge(Predicate<Node> predicate, Node node) {
        return nodesById.find(n -> predicate.test(n.value))
            .map(n -> update(n.id(), n.value))
            .getOrElse(() -> add(node));
    }

    public Graph removeDetach(Predicate<Node> predicate) {
        Set<Long> toRemove = nodes
            .filter(pair -> predicate.test(pair._2))
            .map(Tuple2::_1).toSet();

        TreeMap<Long, RelationshipBetween> newRels = relationships
            .filter(r -> !toRemove.contains(r._2.from.value) && !toRemove.contains(r._2.to.value));

        TreeMap<Long, Node> newNodes = nodes
            .filter(e -> toRemove.contains(e._1));

        return new Graph(nextId, newNodes, newRels);
    }

    public Graph add(RelationshipBetween relationship) {
        return new Graph(nextId + 1, nodes, relationships.put(nextId, relationship));
    }

    public Graph createRelationshipBetween(Predicate<IdAnd<Node>> from, Relationship relationship, Predicate<IdAnd<Node>> to) {
        Option<RelationshipBetween> newRel =
            findNode(from)
                .flatMap(f -> findNode(to)
                    .map(t -> new RelationshipBetween(relationship, f.id(), t.id())));

        return newRel
            .map(this::add)
            .getOrElse(this);
    }

    public Option<IdAnd<Node>> findNode(Predicate<IdAnd<Node>> predicate) {
        return nodesById.find(predicate);
    }

    public Option<IdAnd<RelationshipBetween>> findRelationshipBetween(Predicate<IdAnd<RelationshipBetween>> predicate) {
        return relationshipsById.find(predicate);
    }
}
