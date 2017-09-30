package com.fredhonorio.neu.query;

import com.fredhonorio.neu.decoder.ResultDecoder;
import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.NParamList;
import com.fredhonorio.neu.type.Properties;
import com.fredhonorio.neu.type.Value;
import javaslang.collection.List;
import javaslang.collection.Seq;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

import static com.fredhonorio.neu.decoder.ResultDecoder.field;
import static com.fredhonorio.neu.decoder.ResultDecoder.nodeProps;
import static com.fredhonorio.neu.query.param.Builder.builder;
import static org.junit.Assert.assertTrue;

public class ReadTest {

//    static final Driver driver = Neo4jInProcess.build().driver();

    private final Driver driver = GraphDatabase.driver("bolt://localhost");

    @Before
    public void init() {
        Var n = Var.of("n");
        Write.writeSession(driver, builder().Match().node(n).s("DETACH DELETE").s(n).build()).get();
    }

    @Test
    public void testParameterAccessSyntax() {
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
    public void nodeProperties() {

        Label X = Label.of("X");
        Var s = Var.of("s");

        Statement create = builder()
            .Create()
            .node(X, Properties.of("id", "1")).s(", ")
            .node(X, Properties.of("id", "2"))
            .build();

        Write.writeSessionX(driver, create).get();

        Seq<String> ids = Read.list(
            driver,
            builder().Match().node(s, X).Return(s)
                .build(),
            field(s.value, nodeProps(field("id", ResultDecoder.String)))).get();

        assertTrue(ids.eq(List.of("1", "2")));
    }

    // TODO: test every basic type (boolean, null, int etc

}