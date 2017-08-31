package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.Statement;
import com.fredhonorio.neu.query.Var;
import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.Properties;
import com.fredhonorio.neu.type.Value;
import javaslang.collection.List;
import org.junit.Test;

import static com.fredhonorio.neu.query.param.Builder.builder;
import static org.junit.Assert.assertEquals;

public class BuilderInjectTest {


    @Test
    public void inject() {
        List<Label> XY = Label.many("X", "Y");
        Var n = Var.of("n");

        Statement build = builder().Match().node(n, XY, Properties.of("id", 1))
            .inject(builder().Where().s(Var.of("s")).s("=").param(Value.value(1)).fragments)
            .build();

        assertEquals(
            "MATCH (n:X:Y {`id`: $_0_0}) WHERE s = $_1",
            build.queryTemplate
        );
    }
}
