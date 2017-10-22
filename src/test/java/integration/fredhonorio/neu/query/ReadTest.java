package integration.fredhonorio.neu.query;

import com.fredhonorio.neu.Neo4jInstance;
import com.fredhonorio.neu.decoder.ResultDecoder;
import com.fredhonorio.neu.op.Interpreter;
import com.fredhonorio.neu.op.Interpreters;
import com.fredhonorio.neu.op.Ops;
import com.fredhonorio.neu.query.*;
import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.NParamList;
import com.fredhonorio.neu.type.Properties;
import com.fredhonorio.neu.type.Value;
import javaslang.Tuple;
import javaslang.collection.List;
import javaslang.collection.Seq;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

import static com.fredhonorio.neu.decoder.ResultDecoder.equal;
import static com.fredhonorio.neu.decoder.ResultDecoder.field;
import static com.fredhonorio.neu.decoder.ResultDecoder.nodeProps;
import static com.fredhonorio.neu.op.Ops.first;
import static com.fredhonorio.neu.op.Ops.list;
import static com.fredhonorio.neu.op.Ops.result;
import static com.fredhonorio.neu.query.param.Builder.builder;
import static org.junit.Assert.assertTrue;

public class ReadTest {

    private Driver driver;

    @Before
    public void init() {
        driver = Neo4jInstance.clean();
    }

    @Test
    public void testParameterAccessSyntax() {

        Interpreter tx = Interpreters.writeSession(driver);

        Statement s = new Statement(
            "CREATE (n:X {name: $x[0][0], flag: $x[1].zoop})",
            Value.paramMap(
                "x",
                new NParamList(List.of(
                    Value.paramList("my-name"),
                    Value.paramMap("zoop", Value.nBoolean(false)))
                )));

        tx.submit(result(s)).get();

        tx.submit(first(
            Statement.of("MATCH (n:X) RETURN n"),
            ResultDecoder.field("n",
                nodeProps(
                    ResultDecoder.map2(
                        field("flag", equal(ResultDecoder.Bool, false)),
                        field("name", equal(ResultDecoder.String, "my-name")),
                        Tuple::of)))
        )).get();
    }

    @Test
    public void nodeProperties() {
        Interpreter tx = Interpreters.writeSession(driver);

        Label X = Label.of("X");
        Var s = Var.of("s");

        Statement create = builder()
            .Create()
            .node(X, Properties.of("id", "1")).s(", ")
            .node(X, Properties.of("id", "2"))
            .build();

        tx.submit(Ops.result(create)).get();

        List<String> ids = tx.submit(
            list(
                builder().Match().node(s, X).Return(s).build(),
                field(s.value, nodeProps(field("id", ResultDecoder.String)))))
            .get();

        assertTrue(ids.eq(List.of("1", "2")));
    }
}