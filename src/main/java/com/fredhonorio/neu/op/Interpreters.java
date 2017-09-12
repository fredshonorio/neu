package com.fredhonorio.neu.op;

import com.fredhonorio.neu.decoder.ResultDecoder;
import com.fredhonorio.neu.query.Statement;
import com.fredhonorio.neu.type.NParamMap;
import com.fredhonorio.neu.type.Node;
import javaslang.concurrent.Future;
import javaslang.control.Try;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import static com.fredhonorio.neu.op.Ops.result;
import static com.fredhonorio.neu.op.Ops.single;
import static com.fredhonorio.neu.type.Value.value;

public class Interpreters {

    public static Interpreter transaction(final Driver driver) {
        return new Interpreter() {
            @Override
            public <T> Try<T> submit(GraphDB<T> op) {
                Try<Session> session = Try.of(driver::session);
                Try<T> res = session
                    .mapTry(s -> s.writeTransaction(tx ->
                            Try.of(() -> op.run(tx)).get()
                        )
                    );

                return session.andThenTry(Session::close) // close session
                    .flatMap(__ -> res);
            }
        };
    }

    public static Interpreter readOnly(final Driver driver) {
        return new Interpreter() {
            @Override
            public <T> Try<T> submit(GraphDB<T> op) {
                Try<Session> session = Try.of(driver::session);
                Try<T> res = session
                    .mapTry(s -> s.writeTransaction(tx ->
                            Try.of(() -> op.run(tx)).get()
                        )
                    );

                return session.andThenTry(Session::close) // close session
                    .flatMap(__ -> res);
            }
        };
    }


    public static void main(String[] args) {

        Driver d = GraphDatabase.driver("bolt://localhost");
        Interpreter tx = transaction(d);
        Interpreter ro = readOnly(d);

        GraphDB<StatementResult> writeX = result(Statement.of("CREATE (:X {a: 1})"));
        GraphDB<StatementResult> writeY = result(Statement.of("CREATE (:Y {b: 2, a: 1})"));

        GraphDB<Integer> readYb = single(
            Statement.of("MATCH (i:Y {b: 2}) RETURN i.a as a"),
            ResultDecoder.field("a", ResultDecoder.Integer)
        );

        GraphDB<Node> readXbyYb = readYb.flatMap(yB ->
            single(
                new Statement("MATCH (i:X {a: $0}) RETURN i", NParamMap.empty().put("0", value(yB))),
                ResultDecoder.field("i", ResultDecoder.Node)))
            .map(n -> n.node);

        tx.submit(GraphDB.sequence(writeX, writeY)).get();

        Node node = ro.submit(readXbyYb).get();

        System.out.println(node);
    }


}

