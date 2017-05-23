package com.fredhonorio.neu.query;

import com.fredhonorio.neu.decoder.ResultDecoder;
import com.fredhonorio.neu.type.*;
import javaslang.Tuple;
import javaslang.collection.*;
import javaslang.control.Try;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Values;

import java.util.Map;

import static com.fredhonorio.neu.type.Value.*;
import static org.neo4j.driver.internal.types.InternalTypeSystem.TYPE_SYSTEM;

public class Read {

    public static <T> Try<Seq<Record>> list(Driver driver, Statement statement) {
        return Write.writeSession(driver, statement)
            .map(r -> Stream.ofAll(r.list()))
            .map(records -> records.map(Read::parseRecord))
            .flatMap(Try::sequence);
    }

    public static <T> Try<Seq<T>> list(Driver driver, Statement statement, ResultDecoder<T> decoder) {
        return Write.writeSession(driver, statement)
            .map(r -> Stream.ofAll(r.list()))
            .map(records -> records
                .map(Read::parseRecord)
                .map(rec -> rec.flatMap(r -> decoder.tryDecode(r.asResult()))))
            .flatMap(Try::sequence);
    }

    public static Try<Record> parseRecord(org.neo4j.driver.v1.Record r) {
        return Try.of(() -> decode(r));
    }

    private static Record decode(org.neo4j.driver.v1.Record record) throws IllegalStateException {
        TreeMap<String, Result> items = List.ofAll(record.keys())
            .map(key -> Tuple.of(key, value(record.get(key))))
            .transform(TreeMap::ofEntries);

        return new Record(items);
    }

    private static Node decodeNode(org.neo4j.driver.v1.types.Node node) throws IllegalStateException {
        return new Node(
            LinkedHashSet.ofAll(List.ofAll(node.labels()).map(Label::of)),
            new Properties(
                TreeMap.ofEntries(
                    List.ofAll(node.keys())
                        .toMap(k -> Tuple.of(k, propValue(node.get(k))))))
        );
    }

    private static Property propValue(Value value) {
        if (value.hasType(TYPE_SYSTEM.BOOLEAN())) {
            return nBoolean(value.asBoolean());
        } else if (value.hasType(TYPE_SYSTEM.FLOAT())) {
            return nFloat(value.asDouble());
        } else if (value.hasType(TYPE_SYSTEM.INTEGER())) {
            return nInteger(value.asInt());
        } else if (value.hasType(TYPE_SYSTEM.STRING())) {
            return nString(value.asString());
        } else if (value.hasType(TYPE_SYSTEM.NULL())) {
            return NNull.instance;
        } else if (value.hasType(TYPE_SYSTEM.LIST())) {
            return listProp(value.asList());
        } else {
            throw new IllegalStateException("Cannot handle type: " + value.type());
        }
    }

    private static Primitive primValue(Value value) throws IllegalStateException {
        if (value.hasType(TYPE_SYSTEM.BOOLEAN())) {
            return nBoolean(value.asBoolean());
        } else if (value.hasType(TYPE_SYSTEM.FLOAT())) {
            return nFloat(value.asDouble());
        } else if (value.hasType(TYPE_SYSTEM.INTEGER())) {
            return nInteger(value.asInt());
        } else if (value.hasType(TYPE_SYSTEM.STRING())) {
            return nString(value.asString());
        } else if (value.hasType(TYPE_SYSTEM.NULL())) {
            return NNull.instance;
        } else {
            throw new IllegalStateException("Cannot handle type: " + value.type());
        }
    }

    private static Result value(Value value) throws IllegalStateException {
        if (value.hasType(TYPE_SYSTEM.NODE())) {
            org.neo4j.driver.v1.types.Node nativeNode = value.asNode();
            long id = nativeNode.id();
            return new NNode(id, decodeNode(nativeNode));
        } else if (value.hasType(TYPE_SYSTEM.PATH())) {
            return new NPath(value.asPath());
        } else if (value.hasType(TYPE_SYSTEM.RELATIONSHIP())) { // test
            return new NRelationship(value.asRelationship());
        } else if (value.hasType(TYPE_SYSTEM.BOOLEAN())) {
            return nBoolean(value.asBoolean());
        } else if (value.hasType(TYPE_SYSTEM.FLOAT())) {
            return nFloat(value.asDouble());
        } else if (value.hasType(TYPE_SYSTEM.STRING())) {
            return nString(value.asString());
        } else if (value.hasType(TYPE_SYSTEM.NULL())) {
            return NNull.instance;
        } else if (value.hasType(TYPE_SYSTEM.MAP())) {
            return map(value.asMap());
        } else if (value.hasType(TYPE_SYSTEM.LIST())) {
            return list(value.asList());
        } else {
            throw new IllegalStateException("Cannot handle type: " + value.type());
        }
    }

    private static NPropList listProp(java.util.List<Object> xs) throws IllegalStateException {
        return List.ofAll(xs)
            .map(Values::value) // can throw
            .map(Read::primValue) // can throw
            .transform(NPropList::new);
    }

    private static NResultList list(java.util.List<Object> xs) throws IllegalStateException {
        return List.ofAll(xs)
            .map(Values::value) // can throw
            .map(Read::value) // can throw
            .transform(NResultList::new);
    }

    private static NResultMap map(Map<String, Object> map) throws IllegalStateException {
        return List.ofAll(map.entrySet())
            .map(e -> Tuple.of(e.getKey(), e.getValue()))
            .map(t -> t.map2(Values::value)) // can throw
            .map(t -> t.map2(Read::value)) // can throw
            .foldLeft(NResultMap.empty, (z, x) -> z.put(x._1, x._2));
    }

//    public static void main(String[] args) {
//        Driver d = GraphDatabase.driver("bolt://localhost", Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig());
//        NeoWriterOp w = new NeoWriterOp(d) {
//        };
//
//        List<NResultMap> r = w.run(Statement.statement("MATCH (n) RETURN n"))
//            .map(Read::list).get();
//
//        r.forEach(System.out::println);
//
//    }
}
