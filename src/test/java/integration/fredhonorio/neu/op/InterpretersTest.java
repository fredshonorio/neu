package integration.fredhonorio.neu.op;

import com.fredhonorio.neu.Neo4jInstance;
import com.fredhonorio.neu.decoder.ResultDecoder;
import com.fredhonorio.neu.op.GraphDB;
import com.fredhonorio.neu.op.Interpreter;
import com.fredhonorio.neu.op.Interpreters;
import com.fredhonorio.neu.query.Statement;
import com.fredhonorio.neu.type.NParamMap;
import com.fredhonorio.neu.type.Node;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.StatementResult;

import static com.fredhonorio.neu.decoder.ResultDecoder.Integer;
import static com.fredhonorio.neu.decoder.ResultDecoder.field;
import static com.fredhonorio.neu.op.Ops.result;
import static com.fredhonorio.neu.op.Ops.single;
import static com.fredhonorio.neu.type.Value.value;
import static org.junit.Assert.*;

public class InterpretersTest {

    private Driver driver;

    @Before
    public void init() {
        driver = Neo4jInstance.clean();
    }

    @Test
    public void interpreters() {
        Interpreter tx = Interpreters.writeTransaction(driver);
        Interpreter ro = Interpreters.readSession(driver);

        GraphDB<StatementResult> writeX = result(Statement.of("CREATE (:X {a: 1})"));
        GraphDB<StatementResult> writeY = result(Statement.of("CREATE (:Y {b: 2, a: 1})"));

        GraphDB<Integer> readYb = single(
            Statement.of("MATCH (i:Y {b: 2}) RETURN i.a as a"),
            field("a", Integer)
        );

        GraphDB<Node> readXbyYb = readYb.flatMap(yB ->
            single(
                new Statement("MATCH (i:X {a: $0}) RETURN i", NParamMap.of("0", value(yB))),
                field("i", ResultDecoder.Node)))
            .map(n -> n.node);

        tx.submit(GraphDB.sequence(writeX, writeY)).get();

        Node node = ro.submit(readXbyYb).get();
    }
}