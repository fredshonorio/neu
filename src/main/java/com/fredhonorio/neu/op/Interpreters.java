package com.fredhonorio.neu.op;

import com.fredhonorio.neu.decoder.ResultDecoder;
import com.fredhonorio.neu.query.Statement;
import com.fredhonorio.neu.type.NParamMap;
import com.fredhonorio.neu.type.Node;
import javaslang.control.Try;
import org.neo4j.driver.v1.*;

import static com.fredhonorio.neu.decoder.ResultDecoder.Integer;
import static com.fredhonorio.neu.decoder.ResultDecoder.Node;
import static com.fredhonorio.neu.decoder.ResultDecoder.field;
import static com.fredhonorio.neu.op.Ops.result;
import static com.fredhonorio.neu.op.Ops.single;
import static com.fredhonorio.neu.type.Value.value;
import static com.fredhonorio.neu.util.TryExtra.unsafe;

public class Interpreters {

    public static Interpreter writeTransaction(final Driver driver) {
        return new Interpreter() {
            @Override
            public <T> Try<T> submit(GraphDB<T> op) {
                Try<Session> session = Try.of(driver::session);
                Try<T> res = session.mapTry(s -> s.writeTransaction(tx -> unsafe(() -> op.run(tx))));
                return session.andThenTry(Session::close) // close session
                    .flatMap(__ -> res);
            }
        };
    }

    public static Interpreter readTransaction(final Driver driver) {
        return new Interpreter() {
            @Override
            public <T> Try<T> submit(GraphDB<T> op) {
                Try<Session> session = Try.of(driver::session);
                Try<T> res = session.mapTry(s -> s.readTransaction(tx -> unsafe(() -> op.run(tx))));
                return session.andThenTry(Session::close) // close session
                    .flatMap(__ -> res);
            }
        };
    }

    public static Interpreter readSession(final Driver driver) {
        return new Interpreter() {
            @Override
            public <T> Try<T> submit(GraphDB<T> op) {
                Try<Session> session = Try.of(() -> driver.session(AccessMode.READ));
                Try<T> res = session.mapTry(s -> unsafe(() -> op.run(s)));
                return session.andThenTry(Session::close) // close session
                    .flatMap(__ -> res);
            }
        };
    }

    public static Interpreter writeSession(final Driver driver) {
        return new Interpreter() {
            @Override
            public <T> Try<T> submit(GraphDB<T> op) {
                Try<Session> session = Try.of(() -> driver.session(AccessMode.WRITE));
                Try<T> res = session.mapTry(s -> unsafe(() -> op.run(s)));
                return session.andThenTry(Session::close) // close session
                    .flatMap(__ -> res);
            }
        };
    }
}

