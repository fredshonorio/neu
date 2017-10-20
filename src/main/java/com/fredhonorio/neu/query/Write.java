package com.fredhonorio.neu.query;

import com.fredhonorio.neu.type.NParamMap;
import com.fredhonorio.neu.type.Parameter;
import javaslang.control.Try;
import org.neo4j.driver.internal.value.*;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.summary.ResultSummary;

@Deprecated
public class Write {

    public static Try<StatementResult> writeSession(Driver driver, Statement statement) {
        return writeSession(driver, statement, res -> res);
    }

    public static <T> Try<T> writeSession(Driver driver, Statement statement, Try.CheckedFunction<StatementResult, T> extract) {
        Try<Session> session = Try.of(driver::session);
        Try<T> result = session.mapTry(s -> s.writeTransaction(tx -> run(tx, statement, extract).get()));
        Try<Session> closed = session.andThen(Session::close);
        return closed.flatMap(ignore -> result); // success only if the session is closed and there is a result
    }

    public static <T> Try<T> run(Transaction tx, Statement statement, Try.CheckedFunction<StatementResult, T> extract) {
        return Try.of(() -> tx.run(toNative(statement))).mapTry(extract);
    }

    public static Try<ResultSummary> writeSessionX(Driver driver, Statement statement) {
        Try<Session> session = Try.of(driver::session);
        Try<ResultSummary> result = session.mapTry(s -> s.run(toNative(statement))).mapTry(e -> e.consume());
        session.andThen(Session::close);
        return result;
    }

    public static org.neo4j.driver.v1.Statement toNative(Statement statement) {
        String q = statement.queryTemplate;
        NParamMap params = statement.params;

        return params.value.isEmpty()
            ? new org.neo4j.driver.v1.Statement(q)
            : new org.neo4j.driver.v1.Statement(q, toNative(params));
    }

    private static Value toNative(Parameter parameter) {
        return parameter.matchParam(
            bool -> BooleanValue.fromBoolean(bool.value),
            floating -> new FloatValue(floating.value),
            integer -> new IntegerValue(integer.value),
            list -> new ListValue(list.value.map(Write::toNative).toJavaArray(Value.class)),
            map -> new MapValue(map.value.mapValues(Write::toNative).toJavaMap()),
            string -> new StringValue(string.value),
            nil -> NullValue.NULL
        );
    }
}
