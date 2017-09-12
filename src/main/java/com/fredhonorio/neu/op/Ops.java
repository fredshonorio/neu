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

    public static GraphDB<StatementResult> result(Statement s) {
        return tx -> tx.run(Write.toNative(s));
    }

    public static GraphDB<StatementResult> result(org.neo4j.driver.v1.Statement s) {
        return tx -> tx.run(s);
    }

    public static GraphDB<List<Record>> records(Statement s) {
        return result(s)
            .mapTry(Ops::records);
    }

    public static <T> GraphDB<List<T>> list(Statement s, ResultDecoder<T> decoder) {
        return records(s)
            .mapTry(rs -> decode(decoder, rs));
    }

    public static <T> GraphDB<T> single(Statement s, ResultDecoder<T> decoder) {
        return records(s)
            .mapTryChecked(records -> Assert.that(records, records.size() == 1, "Expecting a single record, got: " + records.size()))
            .mapTry(records -> decode(decoder, records.take(1)))
            .map(List::head);
    }

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

    // this assumes it's ok to throw exceptions
    private static Try<List<Record>> records(StatementResult r) {
        return Stream.ofAll(r.list())
            .map(Read::parseRecord)
            .transform(Try::sequence)
            .transform(tr -> TryExtra.rethrow(tr, "Failed parsing Record"))
            .map(Seq::toList);
    }
}
