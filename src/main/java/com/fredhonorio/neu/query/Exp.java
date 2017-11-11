package com.fredhonorio.neu.query;

import com.fredhonorio.neu.query.param.Fragment;
import com.fredhonorio.neu.query.param.Fragments;
import com.fredhonorio.neu.type.Parameter;
import com.fredhonorio.neu.util.Strings;
import javaslang.collection.List;

import java.util.function.Function;

import static com.fredhonorio.neu.query.Ref.func;
import static com.fredhonorio.neu.query.param.Fragment.param;
import static com.fredhonorio.neu.query.param.Fragment.str;
import static com.fredhonorio.neu.type.Value.value;

// prefix, infix versions of boolean operations
// prefix binary math
// infix comparison
public class Exp implements Fragments {

    public final List<Fragment> fragments;

    @Override
    public List<Fragment> fragments() {
        return fragments;
    }

    public Exp(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    // INFIX - BOOLEAN
    public Exp or(Exp exp) {
        return infix("OR", exp.fragments);
    }

    public Exp xor(Exp exp) {
        return infix("XOR", exp.fragments);
    }

    public Exp and(Exp exp) {
        return infix("AND", exp.fragments);
    }

    // INFIX - COMPARISON
    public Exp eq(Exp exp) {
        return infix("=", exp.fragments);
    }

    public Exp eq(Parameter param) {
        return infix("=", param(param));
    }

    public Exp contains(String value) {
        return infix("CONTAINS", param(value(value)));
    }

    public Exp startsWith(String value) {
        return infix("STARTS WITH", param(value(value)));
    }

    public Exp endsWith(String value) {
        return infix("ENDS WITH", param(value(value)));
    }

    public Exp isNull() {
        return postfix("IS NULL");
    }

    public Exp isNotNull() {
        return postfix("IS NOT NULL");
    }


    // INFIX - OTHER
    public Exp as(Var var) {
        return infix("as", var.fragments);
    }


    // PREFIX - FUNCTIONS
    public static Ref ID(Var v) {
        return func("ID", v);
    }

    public static Ref type(Var v) {
        return func("type", v);
    }

    public static Exp count(Exp exp) {
        return concat("count(", exp.fragments, ")");
    }

    public static Ref count(Ref exp) {
        return Ref.func("count", exp);
    }

    public static Ref collect(Ref ref) {
        return Ref.func("collect", ref);
    }

    public static Exp collect(Exp exp) {
        return concat("collect(", exp.fragments, ")");
    }

    public static Exp collectDistinct(Exp exp) {
        return concat("collect(distinct", exp.fragments, ")");
    }

    public static Ref collectDistinct(Ref exp) {
        return new Ref(Strings.concat("collect(distinct ", exp.string(), ")"));
    }

    public static Exp countDistinct(Exp exp) {
        return concat("count(distinct", exp.fragments, ")");
    }

    public static Ref countDistinct(Ref r) {
        return new Ref(Strings.concat("count(distinct ", r.string(), ")"));
    }


    // PREFIX - MATH
    public static Exp sum(Exp a, Exp b) {
        return concat("(", a.fragments, "+", b.fragments, ")");
    }

    public static Exp sum(long a, Exp b) {
        return concat("(", List.of(param(value(a))), "+", b.fragments, ")");
    }

    public static Exp sum(Exp a, long b) {
        return concat("(", a.fragments, "+", List.of(param(value(b))), ")");
    }

    public static Exp sum(double a, Exp b) {
        return concat("(", List.of(param(value(a))), "+", b.fragments, ")");
    }

    public static Exp sum(Exp a, double b) {
        return concat("(", a.fragments, "+", List.of(param(value(b))), ")");
    }


    // PREFIX - BOOLEAN
    public static Exp or(Exp... exp) {
        return or(List.of(exp));
    }

    public static Exp not(Exp exp) {
        return new Exp(exp.fragments.prepend(str("NOT")));
    }

    public static Exp or(List<Exp> exp) {
        return booleanOp(exp, "OR");
    }

    public static Exp and(Exp... exp) {
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


    // UTIL
    public static Exp of(String exp) {
        return new Exp(List.of(str(exp)));
    }

    public static Exp par(Exp e) {
        return new Exp(par(e.fragments()));
    }


    // PRIVATE
    private static List<Fragment> par(List<Fragment> frags) {
        return frags.prepend(str("(")).append(str(")"));
    }

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

    private Exp postfix(String op) {
        return infix(op, List.empty());
    }

    private Exp infix(String op, Fragment frag) {
        return infix(op, List.of(frag));
    }

    private Exp infix(String op, List<Fragment> frags) {
        return new Exp(
            this.fragments
                .append(new Fragment.Str(Strings.concat(" ", op, " ")))
                .appendAll(frags)
        );
    }
}
