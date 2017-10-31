package com.fredhonorio.neu.query;

import com.fredhonorio.neu.type.Value;
import org.junit.Test;

import static com.fredhonorio.neu.query.param.Builders.Match;
import static com.fredhonorio.neu.query.param.Paths.node;
import static com.fredhonorio.neu.type.Value.value;
import static org.junit.Assert.assertEquals;

public class VarTest {

    @Test
    public void dotDoesNotIntersperseSpaces() {

        Var n = Var.of("n");
        Field hello = Field.of("hello");

        assertEquals(
            "MATCH (n) WHERE n.hello = $_0 RETURN n",
            Match(node(n))
            .Where(n.dot(hello).eq(value("Z")))
            .Return(n)
            .build().queryTemplate
        );
    }
}