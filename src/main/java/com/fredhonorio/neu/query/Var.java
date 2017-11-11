package com.fredhonorio.neu.query;

import com.fredhonorio.neu.query.param.Fragment;
import javaslang.collection.List;

import static com.fredhonorio.neu.query.param.Fragment.str;


// https://neo4j.com/docs/developer-manual/current/cypher/syntax/variables/
public class Var extends Ref {

    public final String value;

    private Var(String value) {
        super(value);
        this.value = value;
    }

    public static Var of(String name) {
        return new Var(name);
    }

    public static Var var(String name) {
        return new Var(name);
    }

    public Ref dot(ToField f) {
        return Ref.dot(this, f);
    }
}
