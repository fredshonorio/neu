package com.fredhonorio.neu.type;

import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.collection.TreeMap;

public class Util {

    public static String printMap(TreeMap<String, ? extends Value> m) {
        return m
            .toStream()
            .map(t -> t._1 + "=" + t._2.toString())
            .mkString(",");
    }

    public static String printList(Seq<? extends Value> m) {
        return m.mkString(",");
    }

    public static <T> TreeMap<String, T> toTreeMap(Map<String, T> map) {
        return map instanceof TreeMap
            ? (TreeMap<String, T>) map
            : TreeMap.ofEntries(map.toList());
    }
}
