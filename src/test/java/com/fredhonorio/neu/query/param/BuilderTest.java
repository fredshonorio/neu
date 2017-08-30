package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.*;
import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.Node;
import com.fredhonorio.neu.type.Properties;
import javaslang.collection.List;
import javaslang.collection.Seq;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

import static com.fredhonorio.neu.query.param.Builder.builder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BuilderTest {

    private Driver neo = GraphDatabase.driver("bolt://localhost");
    // Driver neo = Neo4jInProcess.build().driver();

    private static final Var n = Var.of("n");

    @Before
    public void init() {
        Write.writeSession(neo, builder().Match().node(n).s("DETACH DELETE").s(n).build());
    }

    private void exec(Next.Final b) {
        Write.writeSession(neo, b.build()).get();
    }

    private Node queryOne() {
        Statement ALL = builder().Match().node(n).Return(n).build();
        Seq<Record> list = Read.list(neo, ALL).get();
        assertTrue("multiple nodes " + list, list.size() == 1);
        return list.head().asResult().value.get("n").get().asNode().get().node;
    }

    // unlabeled

    @Test
    public void unlabeledBasicUnnamed() {
        exec(builder().Create().node());
        assertEquals(
            Node.node(List.empty(), Properties.empty()),
            queryOne()
        );
    }

    @Test
    public void unlabeledBasicNamed() {
        exec(builder().Create().node(n));
        assertEquals(
            Node.node(List.empty(), Properties.empty()),
            queryOne()
        );
    }

    @Test
    public void unlabeledPropsUnnamed() {
        exec(builder().Create().node(Properties.of("id", 1)));
        assertEquals(
            Node.node(List.empty(), Properties.of("id", 1)),
            queryOne()
        );
    }

    @Test
    public void unlabeledPropsNamed() {
        exec(builder().Create().node(n, Properties.of("id", 1)));
        assertEquals(
            Node.node(List.empty(), Properties.of("id", 1)),
            queryOne()
        );
    }

    // labeled (one label)

    @Test
    public void labeled1BasicUnnamed() {
        Label X = Label.of("X");
        exec(builder().Create().node(X));
        assertEquals(
            Node.node(List.of(X), Properties.empty()),
            queryOne()
        );
    }

    @Test
    public void labeled1BasicNamed() {
        Label X = Label.of("X");
        exec(builder().Create().node(n, X));
        assertEquals(
            Node.node(X, Properties.empty()),
            queryOne()
        );
    }

    @Test
    public void labeled1PropsUnnamed() {
        Label X = Label.of("X");
        exec(builder().Create().node(X, Properties.of("id", 1)));
        assertEquals(
            Node.node(X, Properties.of("id", 1)),
            queryOne()
        );
    }

    @Test
    public void labeled1PropsNamed() {
        Label X = Label.of("X");
        exec(builder().Create().node(n, X, Properties.of("id", 1)));
        assertEquals(
            Node.node(X, Properties.of("id", 1)),
            queryOne()
        );
    }

    // labeled (two labels)

    @Test
    public void labeled2BasicUnnamed() {
        List<Label> XY = Label.many("X", "Y");
        exec(builder().Create().node(XY));
        assertEquals(
            Node.node(XY, Properties.empty()),
            queryOne()
        );
    }

    @Test
    public void labeled2BasicNamed() {
        List<Label> XY = Label.many("X", "Y");
        exec(builder().Create().node(n, XY));
        assertEquals(
            Node.node(XY, Properties.empty()),
            queryOne()
        );
    }

    @Test
    public void labeled2PropsUnnamed() {
        List<Label> XY = Label.many("X", "Y");
        exec(builder().Create().node(XY, Properties.of("id", 1)));
        assertEquals(
            Node.node(XY, Properties.of("id", 1)),
            queryOne()
        );
    }

    @Test
    public void labeled2PropsNamed() {
        List<Label> XY = Label.many("X", "Y");
        exec(builder().Create().node(n, XY, Properties.of("id", 1)));
        assertEquals(
            Node.node(XY, Properties.of("id", 1)),
            queryOne()
        );
    }

}