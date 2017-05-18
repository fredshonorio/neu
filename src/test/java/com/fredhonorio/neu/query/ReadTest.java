package com.fredhonorio.neu.query;

import com.fredhonorio.neu.type.Value;
import javaslang.collection.List;
import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.NParamList;
import com.fredhonorio.neu.type.Properties;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;

import static com.fredhonorio.neu.type.Node.node;

public class ReadTest {

    static final Neo4jTestServer server = Neo4jTestServer.build();

    @Test
    public void y() {
        Driver driver = server.driver();

        Statement s = new Statement(
            "CREATE (n:X {name: $x[0][0], flag: $x[1].zoop})",
            Value.paramMap(
                "x",
                new NParamList(List.of(
                    Value.paramList("my-name"),
                    Value.paramMap("zoop", Value.nBoolean(false)))
                )
            )
        );

        Write.writeSession(driver, s);

        Read.list(driver, Statement.of("MATCH (n:X) RETURN n"))
            .forEach(System.out::println);

    }
    @Test
    public void x() {

        Driver driver = server.driver();

        Statement node = Statement.createNode(
            node(Label.of("HELLO"), Properties.of("id", Value.nString("1"))));

        Write.writeSession(driver, node);
        Write.writeSession(driver, node);
        Write.writeSession(driver, node);

        Read.list(driver, Statement.of("MATCH (n) RETURN n"))
            .forEach(System.out::println);
    }

}