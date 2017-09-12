package com.fredhonorio.neu.query;

import com.fredhonorio.neu.type.NResultMap;
import com.fredhonorio.neu.type.Result;
import javaslang.collection.LinkedHashMap;
import javaslang.collection.TreeMap;

public class Record {

    public final LinkedHashMap<String, Result> items;

    public Record(LinkedHashMap<String, Result> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Record(" + items.toList().map(t -> t._1 + '=' + t._2.toString()).mkString(",") + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        return items != null ? items.equals(record.items) : record.items == null;
    }

    @Override
    public int hashCode() {
        return items != null ? items.hashCode() : 0;
    }

    public NResultMap asResult() {
        return new NResultMap(TreeMap.ofEntries(items.toList()));
    }
}
