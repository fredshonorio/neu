package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.Properties;
import com.fredhonorio.neu.type.Value;
import javaslang.collection.LinkedHashSet;
import javaslang.collection.TreeSet;
import javaslang.control.Option;
import org.junit.Test;

import static org.junit.Assert.*;

public class NodeTest {
    @Test
    public void pattern1() throws Exception {
    }

    @Test
    public void pattern() throws Exception {

        String p = Fragment.Node.fNode(
            Option.of("n"),
            LinkedHashSet.of(Label.of("LABEL1"), Label.of("LABEL2")),
            Properties.of("prop1", Value.nBoolean(false))
                .put("prop2", Value.nString("clop")))
            .pattern("x");

        System.out.println(p);
    }

}