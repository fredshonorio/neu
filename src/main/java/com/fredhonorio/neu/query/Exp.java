package com.fredhonorio.neu.query;

import com.fredhonorio.neu.query.param.Fragment;
import com.fredhonorio.neu.query.param.Fragments;
import com.fredhonorio.neu.type.Parameter;
import javaslang.collection.List;

import java.util.function.Function;

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

    public Exp(List<Fragment> fragments) {
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

    public Exp eq(Exp exp) {
        return concat(this.fragments, " = ", exp.fragments);
    }

    public Exp eq(Parameter param) {
        return new Exp(
            this.fragments
                .append(new Fragment.Str("="))
                .append(new Fragment.Param(param))
        );
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

    public static Exp ID(Var v) {
        return concat("ID(", v.fragments,")");
    }

    public static Exp type(Var v) {
        return concat("type(", v.fragments,")");
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

    public static Exp or(Exp...exp) {
        return or(List.of(exp));
    }

    public static Exp not(Exp exp) {
        return new Exp(exp.fragments.prepend(str("NOT")));
    }

    public static Exp or(List<Exp> exp) {
        return booleanOp(exp, "OR");
    }

    public static Exp and(Exp...exp) {
        return and(List.of(exp));
    }

    public static Exp and(List<Exp> exp) {
        return booleanOp(exp, "AND");
    }

    private static Exp booleanOp(List<Exp> exps, String op) {
        return exps.map(e -> e.fragments())
            .intersperse(List.of(str(op)))
            .flatMap(Function.identity())
            .transform(fs -> new Exp(par(fs)));
    }

    public static Exp par(Exp e) {
        return new Exp(par(e.fragments()));
    }

    private static List<Fragment> par(List<Fragment> frags) {
        return frags.prepend(str("(")).append(str(")"));
    }

    @Override
    public List<Fragment> fragments() {
        return fragments;
    }
}
