package com.fredhonorio.neu.type;

import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.TreeMap;

public interface Value {
    // http://neo4j.com/docs/developer-manual/current/drivers/cypher-values/

    public static NBoolean nBoolean(boolean x) {
        return new NBoolean(x);
    }

    public static NBoolean value(boolean x) {
        return new NBoolean(x);
    }

    public static NFloat nFloat(double x) {
        return new NFloat(x);
    }

    public static NFloat value(double x) {
        return new NFloat(x);
    }

    public static NInteger nInteger(long x) {
        return new NInteger(x);
    }

    public static NInteger value(long x) {
        return new NInteger(x);
    }

    public static NString nString(String x) {
        return new NString(x);
    }

    public static NString value(String x) {
        return new NString(x);
    }

    public static NNull nNull() {
        return NNull.instance;
    }

    // param list

    public static NParamList paramList(boolean... xs) {
        return new NParamList(List.ofAll(xs).map(Value::value));
    }

    public static NParamList paramList(double... xs) {
        return new NParamList(List.ofAll(xs).map(Value::value));
    }

    public static NParamList paramList(long... xs) {
        return new NParamList(List.ofAll(xs).map(Value::value));
    }

    public static NParamList paramList(String... xs) {
        return new NParamList(List.of(xs).map(Value::value));
    }

    public static NParamList paramList(Parameter... xs) {
        return new NParamList(List.of(xs));
    }

    public static NParamList paramList(Iterable<Parameter> xs) {
        return new NParamList(List.ofAll(xs));
    }

    // prop list

    public static NPropList propList(boolean... xs) {
        return new NPropList(List.ofAll(xs).map(Value::value));
    }

    public static NPropList propListBoolean(Iterable<Boolean> xs) {
        return new NPropList(List.ofAll(xs).map(Value::value));
    }

    public static NPropList propList(double... xs) {
        return new NPropList(List.ofAll(xs).map(Value::value));
    }

    public static NPropList propListFloat(Iterable<Double> xs) {
        return new NPropList(List.ofAll(xs).map(Value::value));
    }

    public static NPropList propList(long... xs) {
        return new NPropList(List.ofAll(xs).map(Value::value));
    }

    public static NPropList propListInteger(Iterable<Integer> xs) {
        return new NPropList(List.ofAll(xs).map(Value::value));
    }

    public static NPropList propList(String... xs) {
        return new NPropList(List.of(xs).map(Value::value));
    }

    public static NPropList propListString(Iterable<String> xs) {
        return new NPropList(List.ofAll(xs).map(Value::value));
    }

    // param map
    public static NParamMap paramMap(String k, Parameter v) {
        return new NParamMap(TreeMap.of(k, v));
    }

    // TODO: String -> X -> NParamMap, for every primitive X

    public static NParamMap paramMap(Map<String, Parameter> map) {
        return new NParamMap(Util.toTreeMap(map));
    }

    // TODO: NParamMap with java util map

}
