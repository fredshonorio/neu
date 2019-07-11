package com.fredhonorio.neu.type;

import com.fredhonorio.neu.util.Strings;
import javaslang.collection.LinkedHashMap;
import javaslang.collection.Map;
import javaslang.collection.Seq;

public class Util {

    public static String printMap(Map<String, ? extends Value> m) {
        return m
            .map(t -> t._1 + "=" + t._2.toString())
            .toList()
            .transform(Strings.mkString(","));
    }

    public static String printList(Seq<? extends Value> m) {
        return m.toList().map(Value::toString).transform(Strings.mkString(","));
    }

    public static <T> LinkedHashMap<String, T> toLinkedHashMap(Map<String, T> map) {
        return map instanceof LinkedHashMap
            ? (LinkedHashMap<String, T>) map
            : LinkedHashMap.ofEntries(map.toList());
    }
}
