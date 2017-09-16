package com.fredhonorio.neu.op;

import com.fredhonorio.neu.decoder.ResultDecoder;
import com.fredhonorio.neu.query.Read;
import com.fredhonorio.neu.query.Record;
import com.fredhonorio.neu.query.Statement;
import com.fredhonorio.neu.query.Write;
import com.fredhonorio.neu.util.Assert;
import com.fredhonorio.neu.util.TryExtra;
import javaslang.Value;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.collection.Stream;
import javaslang.control.Try;
import org.neo4j.driver.v1.StatementResult;

public class Ops {

    /**
     * An action that executes a {@link Statement} and returns a {@link StatementResult}
     * @param s the statement
     */
    public static GraphDB<StatementResult> result(Statement s) {
        return tx -> tx.run(Write.toNative(s));
    }

    /**
     * An action that executes a native {@link org.neo4j.driver.v1.Statement} and returns a {@link StatementResult}
     * @param s the statement
     * @return
     */
    public static GraphDB<StatementResult> result(org.neo4j.driver.v1.Statement s) {
        return tx -> tx.run(s);
    }

    /**
     * An action that executes a {@link Statement} and returns the whole list of {@link Record}s
     * @param s the statement
     */
    public static GraphDB<List<Record>> records(Statement s) {
        return result(s)
            .mapTry(Ops::records);
    }

    /**
     * An action that executes a {@link Statement} and returns the whole list of {@link Record}s, decoded by the given
     * decoder.
     * While decoding, the record is converted to a {@link com.fredhonorio.neu.type.NResultMap}.
     * @param s the statement
     * @param decoder the decoder
     * @param <T> the type of the decoded value
     */
    public static <T> GraphDB<List<T>> list(Statement s, ResultDecoder<T> decoder) {
        return records(s)
            .mapTry(rs -> decode(decoder, rs));
    }

    /**
     * An action that executes a {@link Statement} and returns the only {@link Record}, decoded by the given decoder.
     * If there are no records, or more than one, the operation will fail.
     * While decoding, the record is converted to a {@link com.fredhonorio.neu.type.NResultMap}.
     * @param s the statement
     * @param decoder the decoder
     * @param <T> the type of the decoded value
     */
    public static <T> GraphDB<T> single(Statement s, ResultDecoder<T> decoder) {
        return records(s)
            .mapTryChecked(records -> Assert.that(records, records.size() == 1, "Expecting a single record, got: " + records.size()))
            .mapTry(records -> decode(decoder, records.take(1)))
            .map(List::head);
    }

    /**
     * An action that executes a {@link Statement} and returns the first {@link Record}, decoded by the given decoder.
     * If there are no records, the operation will fail.
     * While decoding, the record is converted to a {@link com.fredhonorio.neu.type.NResultMap}.
     * @param s the statement
     * @param decoder the decoder
     * @param <T> the type of the decoded value
     */
    public static <T> GraphDB<T> first(Statement s, ResultDecoder<T> decoder) {
        return records(s)
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
