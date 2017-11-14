package com.fredhonorio.neu.query;

import com.fredhonorio.neu.query.param.Fragment;
import com.fredhonorio.neu.query.param.Fragments;
import com.fredhonorio.neu.type.NParamList;
import com.fredhonorio.neu.type.Parameter;
import com.fredhonorio.neu.util.Strings;
import javaslang.collection.List;

import java.util.function.Function;

import static com.fredhonorio.neu.query.Ref.func;
import static com.fredhonorio.neu.query.param.Fragment.param;
import static com.fredhonorio.neu.query.param.Fragment.str;
import static com.fredhonorio.neu.type.Value.value;

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
        return infix("OR", exp);
    }

    public Exp xor(Exp exp) {
        return infix("XOR", exp);
    }

    public Exp and(Exp exp) {
        return infix("AND", exp);
    }

    // INFIX - COMPARISON
    public Exp eq(Exp exp) {
        return infix("=", exp);
    }

    public Exp eq(Parameter param) {
        return infix("=", param);
    }

    public Exp ineq(Exp exp) {
        return infix("<>", exp);
    }

    public Exp ineq(Parameter param) {
        return infix("<>", param);
    }

    public Exp lt(Exp exp) {
        return infix("<", exp);
    }

    public Exp lt(Parameter param) {
        return infix("<", param);
    }

    public Exp gt(Exp exp) {
        return infix(">", exp);
    }

    public Exp gt(Parameter param) {
        return infix(">", param);
    }

    public Exp lte(Exp exp) {
        return infix("<=", exp);
    }

    public Exp lte(Parameter param) {
        return infix("<=", param);
    }

    public Exp gte(Exp exp) {
        return infix(">=", exp);
    }

    public Exp gte(Parameter param) {
        return infix(">=", param);
    }

    public Exp isNull() {
        return postfix("IS NULL");
    }

    public Exp isNotNull() {
        return postfix("IS NOT NULL");
    }

    public Exp startsWith(String value) {
        return infix("STARTS WITH", value(value));
    }

    public Exp endsWith(String value) {
        return infix("ENDS WITH", value(value));
    }

    public Exp contains(String value) {
        return infix("CONTAINS", value(value));
    }

    // INFIX - LIST
    public Exp in(NParamList list) {
        return infix("IN", list);
    }

    public Exp in(Exp list) {
        return infix("IN", list);
    }

    public Exp index(Exp idx) {
        return new Exp(
            this.fragments
                .append(str("["))
                .appendAll(idx.fragments())
                .append(str("]"))
        );
    }

    public Exp index(long i) {
        return index(new Exp(List.of(param(value(i)))));
    }

    // INFIX - OTHER
    public Exp as(Var var) {
        return infix("as", var.fragments);
    }

    // string/list concatenation
    public Exp plus(Parameter p) {
        return infix("+", p);
    }

    // string/list concatenation (works for numbers as well, but static methods are prefered
    public Exp plus(Exp e) {
        return infix("+", e);
    }

    public Exp regexMatch(Exp e) {
        return infix("=~", e);
    }

    public Exp regexMatch(String regex) {
        return infix("=~", value(regex));
    }

    // PREFIX - FUNCTIONS
    public static Exp distinct(Exp exp) {
        return concat("distinct", exp.fragments, "");
    }

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
    //                SUM
    public static Exp sum(Exp a, Exp b) {
        return infixPar("+", a, b);
    }

    public static Exp sum(long a, Exp b) {
        return infixPar("+", a, b);
    }

    public static Exp sum(Exp a, long b) {
        return infixPar("+", a, b);
    }

    public static Exp sum(double a, Exp b) {
        return infixPar("+", a, b);
    }

    public static Exp sum(Exp a, double b) {
        return infixPar("+", a, b);
    }

    //                SUB
    public static Exp sub(Exp a, Exp b) {
        return infixPar("-", a, b);
    }

    public static Exp sub(long a, Exp b) {
        return infixPar("-", a, b);
    }

    public static Exp sub(Exp a, long b) {
        return infixPar("-", a, b);
    }

    public static Exp sub(double a, Exp b) {
        return infixPar("-", a, b);
    }

    public static Exp sub(Exp a, double b) {
        return infixPar("-", a, b);
    }


    //                MUL
    public static Exp mul(Exp a, Exp b) {
        return infixPar("*", a, b);
    }

    public static Exp mul(long a, Exp b) {
        return infixPar("*", a, b);
    }

    public static Exp mul(Exp a, long b) {
        return infixPar("*", a, b);
    }

    public static Exp mul(double a, Exp b) {
        return infixPar("*", a, b);
    }

    public static Exp mul(Exp a, double b) {
        return infixPar("*", a, b);
    }


    //                DIV
    public static Exp div(Exp a, Exp b) {
        return infixPar("/", a, b);
    }

    public static Exp div(long a, Exp b) {
        return infixPar("/", a, b);
    }

    public static Exp div(Exp a, long b) {
        return infixPar("/", a, b);
    }

    public static Exp div(double a, Exp b) {
        return infixPar("/", a, b);
    }

    public static Exp div(Exp a, double b) {
        return infixPar("/", a, b);
    }

    //                MOD
    public static Exp mod(Exp a, Exp b) {
        return infixPar("%", a, b);
    }

    public static Exp mod(long a, Exp b) {
        return infixPar("%", a, b);
    }

    public static Exp mod(Exp a, long b) {
        return infixPar("%", a, b);
    }

    public static Exp mod(double a, Exp b) {
        return infixPar("%", a, b);
    }

    public static Exp mod(Exp a, double b) {
        return infixPar("%", a, b);
    }

    //                POW
    public static Exp pow(Exp a, Exp b) {
        return infixPar("^", a, b);
    }

    public static Exp pow(long a, Exp b) {
        return infixPar("^", a, b);
    }

    public static Exp pow(Exp a, long b) {
        return infixPar("^", a, b);
    }

    public static Exp pow(double a, Exp b) {
        return infixPar("^", a, b);
    }

    public static Exp pow(Exp a, double b) {
        return infixPar("^", a, b);
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

    // TODO: it's weird to represent '*' with a Ref, we might need a better solution so that
    // prefix functions don't produce spaces between the parens and the argument
    public static Ref asterisk() {
        return all();
    }

    public static Ref all() {
        return new Ref("*");
    }

    // TODO: unary negate

    // PRIVATE
    private static Exp infixPar(String op, List<Fragment> a, List<Fragment> b) {
        return new Exp(par(a.append(str(op)).appendAll(b)));
    }

    private static Exp infixPar(String op, Exp a, Exp b) {
        return infixPar(op, a.fragments(), b.fragments());
    }

    private static Exp infixPar(String op, long a, Exp b) {
        return infixPar(op, List.of(param(value(a))), b.fragments());
    }

    private static Exp infixPar(String op, Exp a, long b) {
        return infixPar(op, a.fragments(), List.of(param(value(b))));
    }

    private static Exp infixPar(String op, double a, Exp b) {
        return infixPar(op, List.of(param(value(a))), b.fragments());
    }

    private static Exp infixPar(String op, Exp a, double b) {
        return infixPar(op, a.fragments(), List.of(param(value(b))));
    }

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

    private Exp infix(String op, Parameter param) {
        return infix(op, List.of(new Fragment.Param(param)));
    }

    private Exp infix(String op, Exp exp) {
        return infix(op, exp.fragments());
    }

    private Exp infix(String op, List<Fragment> frags) {
        return new Exp(
            this.fragments
                .append(str(op))
                .appendAll(frags)
        );
    }
}
