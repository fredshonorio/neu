package com.fredhonorio.neu.query;

import com.fredhonorio.neu.query.param.Fragment;
import com.fredhonorio.neu.query.param.Fragments;
import javaslang.collection.List;

import static com.fredhonorio.neu.query.param.Fragment.param;
import static com.fredhonorio.neu.query.param.Fragment.str;
import static com.fredhonorio.neu.type.Value.value;

public class Exp implements Fragments {

    static Exp concat(List<Fragment> a, String b, List<Fragment> c) {
        return new Exp(a.append(str(b)).appendAll(c));
    }

    static Exp concat(String a, List<Fragment> b, String c) {
        return new Exp(b.prepend(str(a)).append(str(c)));
    }


    static Exp concat(String a, List<Fragment> b, String c, List<Fragment> d, String e) {
        return new Exp(
            List.<Fragment>of(str(a))
                .appendAll(b)
                .append(str(c))
                .appendAll(d)
                .append(str(e)));
    }

    public final List<Fragment> fragments;

    Exp(List<Fragment> fragments) {
        this.fragments = fragments;
    }


    public Exp as(Var var) {
        return concat(this.fragments, " as ", var.fragments);
    }

    public Exp or(Exp exp) {
        return concat(this.fragments, " OR ", exp.fragments);
    }

    public Exp xor(Exp exp) {
        return concat(this.fragments, " XOR ", exp.fragments);
    }

    public Exp and(Exp exp) {
        return concat(this.fragments, " AND ", exp.fragments);
    }


    public Exp sum(Exp a, Exp b) {
        return concat("(", a.fragments, "+", b.fragments, ")");
    }

    // sum long
    public Exp sum(long a, Exp b) {
        return concat("(", List.of(param(value(a))), "+", b.fragments, ")");
    }

    public Exp sum(Exp a, long b) {
        return concat("(", a.fragments, "+", List.of(param(value(b))), ")");
    }

    // sum double
    public Exp sum(double a, Exp b) {
        return concat("(", List.of(param(value(a))), "+", b.fragments, ")");
    }

    public Exp sum(Exp a, double b) {
        return concat("(", a.fragments, "+", List.of(param(value(b))), ")");
    }


    public static Exp of(String exp) {
        return new Exp(List.of(str(exp)));
    }

    public static Exp count(Exp exp) {
        return concat("count(", exp.fragments, ")");
    }

    public static Exp collect(Exp exp) {
        return concat("collect(", exp.fragments, ")");
    }

    public static Exp collectDistinct(Exp exp) {
        return concat("collect(distinct", exp.fragments, ")");
    }

    public static Exp countDistinct(Exp exp) {
        return concat("count(distinct", exp.fragments, ")");
    }

    @Override
    public List<Fragment> fragments() {
        return fragments;
    }
}
