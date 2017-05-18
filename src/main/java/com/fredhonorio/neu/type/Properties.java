package com.fredhonorio.neu.type;

import javaslang.collection.TreeMap;

public class Properties {
    private final TreeMap<String, Property> properties;

    public Properties(TreeMap<String, Property> properties) {
        this.properties = properties;
    }

    public TreeMap<String, Property> asMap() {
        return properties;
    }

    public Properties put(String k, Property v) {
        return new Properties(properties.put(k, v));
    }

    public static Properties empty() {
        return new Properties(TreeMap.empty());
    }

    public static Properties of(String k, Property v) {
        return new Properties(TreeMap.of(k, v));
    }

    public String describe() {
        return properties
            .toList()
            .map(t -> t._1 + "=" + t._2)
            .mkString(", ");
    }

    @Override
    public String toString() {
        return "Properties(" + describe() + ')';
    }
}
