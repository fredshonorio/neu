package com.fredhonorio.neu.query;

import com.fredhonorio.neu.query.param.Fragment;
import com.fredhonorio.neu.util.Strings;
import javaslang.collection.List;

public class Ref extends Exp {

    private final String str;

    public Ref(String str) {
        super(List.of(Fragment.str(str)));
        this.str = str;
    }

    static Ref func(String name, Ref v) {
        return new Ref(Strings.concat(name, "(", v.string(), ")"));
    }

    static Ref dot(Var v, ToField f) {
        return new Ref(Strings.concat(v.value, ".", f.field().fieldName()));
    }

    public String string() {
        return str;
    }
}
