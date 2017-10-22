package integration.fredhonorio.neu.query.param;

import com.fredhonorio.neu.Neo4jInstance;
import com.fredhonorio.neu.decoder.ResultDecoder;
import com.fredhonorio.neu.op.Interpreter;
import com.fredhonorio.neu.op.Interpreters;
import com.fredhonorio.neu.op.Ops;
import com.fredhonorio.neu.query.Statement;
import com.fredhonorio.neu.query.Var;
import com.fredhonorio.neu.query.param.Builder;
import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.Node;
import com.fredhonorio.neu.type.Properties;
import javaslang.collection.List;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;

import static com.fredhonorio.neu.query.param.Builder.builder;
import static org.junit.Assert.assertEquals;

public class BuilderTest {

    private Driver driver;

    @Before
    public void init() {
        driver = Neo4jInstance.clean();
    }

    private static final Var n = Var.of("n");

    private void exec(Builder b) {
        Interpreters.writeSession(driver)
            .submit(Ops.result(b.build())).get();
    }

    private Node queryOne() {
        Statement ALL = builder().Match().node(n).Return(n).build();
        Interpreter i = Interpreters.readSession(driver);
        return i.submit(Ops.first(ALL, ResultDecoder.field("n", ResultDecoder.Node)).map(n -> n.node)).get();
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