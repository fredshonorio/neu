package com.fredhonorio.neu.query;

import com.fredhonorio.neu.type.Parameter;
import javaslang.control.Try;
import com.fredhonorio.neu.type.NParamMap;
import org.neo4j.driver.internal.value.*;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.summary.ResultSummary;

public class Write {

    public static Try<StatementResult> writeSession(Driver driver, Statement statement) {
        Try<Session> session = Try.of(driver::session);
        Try<StatementResult> result = session.mapTry(s -> s.run(toNative(statement)));
        session.andThen(Session::close);
        return result;
    }

    public static Try<ResultSummary> writeSessionX(Driver driver, Statement statement) {
        Try<Session> session = Try.of(driver::session);
        Try<ResultSummary> result = session.mapTry(s -> s.run(toNative(statement))).mapTry(e -> e.consume());
        session.andThen(Session::close);
        return result;
    }

    private static org.neo4j.driver.v1.Statement toNative(Statement statement) {
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
