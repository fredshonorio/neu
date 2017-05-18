package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.Properties;
import com.fredhonorio.neu.type.Value;
import javaslang.collection.TreeSet;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fred on 18/05/17.
 */
public class NodeTest {
    @Test
    public void pattern() throws Exception {

        String p = new Fragment.Node("n",
            TreeSet.of(Label.of("LABEL1"), Label.of("LABEL2")),
            Properties.of("prop1", Value.nBoolean(false))
                .put("prop2", Value.nString("clop")))
            .pattern();

        System.out.println(p);
    }

}