package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.Var;
import com.fredhonorio.neu.type.Properties;
import com.fredhonorio.neu.type.Type;
import org.junit.Test;

import java.util.function.Function;

import static com.fredhonorio.neu.query.param.Builder.builder;
import static org.junit.Assert.assertEquals;

public class BuilderTestTo {

    public void testQuery(Function<Builder.EmptyB, Builder.StrB> x, String expected) {
        assertEquals(
            expected,
            x.apply(builder()).build().queryTemplate
        );
    }

    public static Properties PROP = Properties.of("x", 1);
    public static Type TYPE = Type.of("T");
    public static Var VAR = Var.of("r");

    @Test
    public void _00x() {
        testQuery(
            b -> b.Match().node().to().node().Return("x"),
            "MATCH () --> () RETURN x"
        );

        testQuery(
            b -> b.Match().node().to(PROP).node().Return("x"),
            "MATCH () -[{`x`: $_0_0}]-> () RETURN x"
        );
    }

    @Test
    public void _01x() {
        testQuery(
            b -> b.Match().node().to(TYPE).node().Return("x"),
            "MATCH () -[:T]-> () RETURN x"
        );

        testQuery(
            b -> b.Match().node().to(TYPE, PROP).node().Return("x"),
            "MATCH () -[:T {`x`: $_0_0}]-> () RETURN x"
        );
    }

    @Test
    public void _10x() {
        testQuery(
            b -> b.Match().node().to(VAR).node().Return("x"),
            "MATCH () -[r]-> () RETURN x"
        );

        testQuery(
            b -> b.Match().node().to(VAR, PROP).node().Return("x"),
            "MATCH () -[r {`x`: $_0_0}]-> () RETURN x"
        );
    }

    @Test
    public void _11x() {
        testQuery(
            b -> b.Match().node().to(VAR, TYPE).node().Return("x"),
            "MATCH () -[r:T]-> () RETURN x"
        );

        testQuery(
            b -> b.Match().node().to(VAR, TYPE, PROP).node().Return("x"),
            "MATCH () -[r:T {`x`: $_0_0}]-> () RETURN x"
        );
    }
}