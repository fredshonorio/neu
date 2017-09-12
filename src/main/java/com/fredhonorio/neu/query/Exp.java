package com.fredhonorio.neu.query;

import static com.fredhonorio.neu.util.Strings.concat;

public class Exp extends Boxed<String> {
    protected Exp(String value) {
        super(value);
    }

    public String asString() {
        return value;
    }

    public Exp as(Var var) {
        return new Exp(concat(asString(), " as ", var.asString()));
    }

    public static Exp of(String exp) {
        return new Exp(exp);
    }

    public static Exp count(Exp exp) {
        return new Exp(concat("count(", exp.asString(), ")"));
    }

    public static Exp collect(Exp exp) {
        return new Exp(concat("collect(", exp.asString(), ")"));
    }

    public static Exp collectDistinct(Exp exp) {
        return new Exp(concat("collect(distinct", exp.asString(), ")"));
    }

    public static Exp countDistinct(Exp exp) {
        return new Exp(concat("count(distinct", exp.asString(), ")"));
    }
}
