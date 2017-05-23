package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.*;
import com.fredhonorio.neu.type.*;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.control.Try;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

import static com.fredhonorio.neu.type.Value.nInteger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BuilderTest {

    // Driver neo = GraphDatabase.driver("bolt://localhost");
    Driver neo = Neo4jInProcess.build().driver();

    @Before
    public void init() {
        Write.writeSession(neo, Builder.of("MATCH (n) DETACH DELETE n").build());
    }

    public void exec(Next.Final b) {
        Write.writeSession(neo, b.build()).get();
    }

    public Node queryOne() {
        Statement ALL = Builder.of("MATCH ").node("n").s(" RETURN n").build();
        Seq<Record> list = Read.list(neo, ALL).get();
        assertTrue("multiple nodes " + list, list.size() == 1);
        return list.head().asResult().value.get("n").get().asNode().get().node;
    }

    // unlabeled

    @Test
    public void unlabeledBasicUnnamed() {
        exec(Builder.of("CREATE ").node());
        assertEquals(
            Node.node(List.empty(), Properties.empty()),
            queryOne()
        );
    }

    @Test
    public void unlabeledBasicNamed() {
        exec(Builder.of("CREATE ").node("n"));
        assertEquals(
            Node.node(List.empty(), Properties.empty()),
            queryOne()
        );
    }

    @Test
    public void unlabeledPropsUnnamed() {
        exec(Builder.of("CREATE ").node(Properties.of("id", nInteger(1))));
        assertEquals(
            Node.node(List.empty(), Properties.of("id", nInteger(1))),
            queryOne()
        );
    }

    @Test
    public void unlabeledPropsNamed() {
        exec(Builder.of("CREATE ").node("n", Properties.of("id", nInteger(1))));
        assertEquals(
            Node.node(List.empty(), Properties.of("id", nInteger(1))),
            queryOne()
        );
    }

    // labeled (one label)

    @Test
    public void labeled1BasicUnnamed() {
        Label X = Label.of("X");
        exec(Builder.of("CREATE ").node(X));
        assertEquals(
            Node.node(List.of(X), Properties.empty()),
            queryOne()
        );
    }

    @Test
    public void labeled1BasicNamed() {
        Label X = Label.of("X");
        exec(Builder.of("CREATE ").node("n", X));
        assertEquals(
            Node.node(X, Properties.empty()),
            queryOne()
        );
    }

    @Test
    public void labeled1PropsUnnamed() {
        Label X = Label.of("X");
        exec(Builder.of("CREATE ").node(X, Properties.of("id", nInteger(1))));
        assertEquals(
            Node.node(X, Properties.of("id", nInteger(1))),
            queryOne()
        );
    }

    @Test
    public void labeled1PropsNamed() {
        Label X = Label.of("X");
        exec(Builder.of("CREATE ").node("n", X, Properties.of("id", nInteger(1))));
        assertEquals(
            Node.node(X, Properties.of("id", nInteger(1))),
            queryOne()
        );
    }

    // labeled (two labels)

    @Test
    public void labeled2BasicUnnamed() {
        List<Label> XY = Label.many("X", "Y");
        exec(Builder.of("CREATE ").node(XY));
        assertEquals(
            Node.node(XY, Properties.empty()),
            queryOne()
        );
    }

    @Test
    public void labeled2BasicNamed() {
        List<Label> XY = Label.many("X", "Y");
        exec(Builder.of("CREATE ").node("n", XY));
        assertEquals(
            Node.node(XY, Properties.empty()),
            queryOne()
        );
    }

    @Test
    public void labeled2PropsUnnamed() {
        List<Label> XY = Label.many("X", "Y");
        exec(Builder.of("CREATE ").node(XY, Properties.of("id", nInteger(1))));
        assertEquals(
            Node.node(XY, Properties.of("id", nInteger(1))),
            queryOne()
        );
    }

    @Test
    public void labeled2PropsNamed() {
        List<Label> XY = Label.many("X", "Y");
        exec(Builder.of("CREATE ").node("n", XY, Properties.of("id", nInteger(1))));
        assertEquals(
            Node.node(XY, Properties.of("id", nInteger(1))),
            queryOne()
        );
    }

}