package com.fredhonorio.neu.op;

import com.fredhonorio.neu.decoder.ResultDecoder;
import com.fredhonorio.neu.query.Read;
import com.fredhonorio.neu.query.Record;
import com.fredhonorio.neu.query.Statement;
import com.fredhonorio.neu.query.Write;
import com.fredhonorio.neu.util.Assert;
import com.fredhonorio.neu.util.TryExtra;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.collection.Stream;
import javaslang.control.Try;
import org.neo4j.driver.v1.StatementResult;

public class Ops {

    /**
     * An action that executes a {@link Statement} and returns a {@link StatementResult}
     *
     * @param statement the statement
     * @return the action
     */
    public static GraphDB<StatementResult> result(Statement statement) {
        return tx -> tx.run(Write.toNative(statement));
    }

    /**
     * An action that executes a native {@link org.neo4j.driver.v1.Statement} and returns a {@link StatementResult}
     *
     * @param statement the statement
     * @return the action
     */
    public static GraphDB<StatementResult> result(org.neo4j.driver.v1.Statement statement) {
        return tx -> tx.run(statement);
    }

    /**
     * An action that executes a {@link Statement} and returns the whole list of {@link Record}s
     *
     * @param statement the statement
     * @return the action
     */
    public static GraphDB<List<Record>> records(Statement statement) {
        return result(statement)
            .mapTry(Ops::records);
    }

    /**
     * An action that executes a {@link Statement} and returns the whole list of {@link Record}s, decoded by the given
     * decoder.
     * While decoding, the record is converted to a {@link com.fredhonorio.neu.type.NResultMap}.
     *
     * @param statement the statement
     * @param decoder   the decoder
     * @param <T>       the type of the decoded value
     * @return the action
     */
    public static <T> GraphDB<List<T>> list(Statement statement, ResultDecoder<T> decoder) {
        return records(statement)
            .mapTry(rs -> decode(decoder, rs));
    }

    /**
     * An action that executes a {@link Statement} and returns the only {@link Record}, decoded by the given decoder.
     * If there are no records, or more than one, the operation will fail.
     * While decoding, the record is converted to a {@link com.fredhonorio.neu.type.NResultMap}.
     *
     * @param statement the statement
     * @param decoder   the decoder
     * @param <T>       the type of the decoded value
     * @return the action
     */
    public static <T> GraphDB<T> single(Statement statement, ResultDecoder<T> decoder) {
        return records(statement)
            .mapTryChecked(records -> Assert.that(records, records.size() == 1, "Expecting a single record, got: " + records.size()))
            .mapTry(records -> decode(decoder, records.take(1)))
            .map(List::head);
    }

    /**
     * An action that executes a {@link Statement} and returns the first {@link Record}, decoded by the given decoder.
     * If there are no records, the operation will fail.
     * While decoding, the record is converted to a {@link com.fredhonorio.neu.type.NResultMap}.
     *
     * @param statement the statement
     * @param decoder   the decoder
     * @param <T>       the type of the decoded value
     * @return the action
     */
    public static <T> GraphDB<T> first(Statement statement, ResultDecoder<T> decoder) {
        return records(statement)
            .mapTryChecked(records -> Assert.that(records, records.size() >= 1, "Expecting non-empty list of records"))
            .mapTry(records -> decode(decoder, records.take(1)))
            .map(List::head);
    }

    private static <T> Try<List<T>> decode(ResultDecoder<T> decoder, List<Record> records) {
        return records.toStream() // we can't be sure that decoding is expensive, so we use stream to decode lazily, sequence will short-circuit
            .map(Record::asResult)
            .map(decoder::tryDecode)
            .transform(Try::sequence)
            .transform(tr -> TryExtra.rethrow(tr, "Failed decoding Record into Result"))
            .map(Seq::toList);
    }

    private static Try<List<Record>> records(StatementResult r) {
        return Stream.ofAll(r.list())
            .map(Read::parseRecord)
            .transform(Try::sequence)
            .transform(tr -> TryExtra.rethrow(tr, "Failed parsing Record"))
            .map(Seq::toList);
    }
}
