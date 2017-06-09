package com.fredhonorio.neu.type;

import javaslang.collection.TreeMap;

public class Properties {
    private final TreeMap<String, Property> properties;

    // TODO: sanitize key names
    public Properties(TreeMap<String, Property> properties) {
        this.properties = properties;
    }

    public TreeMap<String, Property> asMap() {
        return properties;
    }

    public Properties put(String k, Property v) {
        return new Properties(properties.put(k, v));
    }

    public Properties put(String k, boolean v) {
        return new Properties(properties.put(k, Value.nBoolean(v)));
    }

    public Properties put(String k, String v) {
        return new Properties(properties.put(k, Value.nString(v)));
    }

    public Properties put(String k, double v) {
        return new Properties(properties.put(k, Value.nFloat(v)));
    }

    public Properties put(String k, long v) {
        return new Properties(properties.put(k, Value.nInteger(v)));
    }

    public static Properties of(String k, Property v) {
        return new Properties(TreeMap.of(k, v));
    }

    public static Properties of(String k, boolean v) {
        return new Properties(TreeMap.of(k, Value.nBoolean(v)));
    }

    public static Properties of(String k, String v) {
        return new Properties(TreeMap.of(k, Value.nString(v)));
    }

    public static Properties of(String k, double v) {
        return new Properties(TreeMap.of(k, Value.nFloat(v)));
    }

    public static Properties of(String k, long v) {
        return new Properties(TreeMap.of(k, Value.nInteger(v)));
    }

    public static Properties empty() {
        return new Properties(TreeMap.empty());
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public Result asResult() {
        return new NResultMap(properties.mapValues(Property::asResult));
    }

    public String describe() {
        return properties
            .toList()
            .map(t -> t._1 + "=" + t._2)
            .mkString(", ");
    }

    public String pattern(String nodeName) {
        return properties.keySet()
            .map(key -> key + ": $" + nodeName + "." + key)
            .mkString("{", ",", "}");
    }

    @Override
    public String toString() {
        return "Properties(" + describe() + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Properties that = (Properties) o;

        return properties != null ? properties.equals(that.properties) : that.properties == null;
    }

    @Override
    public int hashCode() {
        return properties != null ? properties.hashCode() : 0;
    }
}
