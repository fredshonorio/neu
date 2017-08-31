package com.fredhonorio.neu.query;

import static com.fredhonorio.neu.util.Strings.concat;

// https://neo4j.com/docs/developer-manual/current/cypher/syntax/variables/
public class Var extends Exp {

    private Var(String value) {
        super(value);
    }

    public static Var of(String name) {
        return new Var(name);
    }

    public Exp dot(Field f) {
        return new Exp(concat(asString(), ".", f.fieldName()));
    }
}
