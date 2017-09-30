package com.fredhonorio.neu.type;

import com.fredhonorio.neu.util.Strings;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.collection.TreeMap;

public class Util {

    public static String printMap(TreeMap<String, ? extends Value> m) {
        return m
            .map(t -> t._1 + "=" + t._2.toString())
            .toList()
            .transform(Strings.mkString(","));
    }

    public static String printList(Seq<? extends Value> m) {
        return m.toList().map(Value::toString).transform(Strings.mkString(","));
    }

    public static <T> TreeMap<String, T> toTreeMap(Map<String, T> map) {
        return map instanceof TreeMap
            ? (TreeMap<String, T>) map
            : TreeMap.ofEntries(map.toList());
    }
}
