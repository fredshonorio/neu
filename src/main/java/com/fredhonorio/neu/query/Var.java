package com.fredhonorio.neu.query;

import com.fredhonorio.neu.query.param.Fragment;
import javaslang.collection.List;

import static com.fredhonorio.neu.query.param.Fragment.str;


// https://neo4j.com/docs/developer-manual/current/cypher/syntax/variables/
public class Var extends Exp {

    public final String value;

    private Var(String value) {
        super(List.of(str(value)));
        this.value = value;
    }

    public static Var of(String name) {
        return new Var(name);
    }

    public static Var var(String name) {
        return new Var(name);
    }

    public Exp dot(Field f) {
        return new Exp(
            List.<Fragment>of(
                new Fragment.Str(value, false),
                new Fragment.Str(".", false),
                str(f.fieldName()))
        );
    }

    }
}
