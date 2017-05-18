package com.fredhonorio.neu.type;

import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.TreeMap;

public interface Value {
    // http://neo4j.com/docs/developer-manual/current/drivers/cypher-values/

    public static NBoolean nBoolean(boolean x) {
        return new NBoolean(x);
    }

    public static NFloat nFloat(double x) {
        return new NFloat(x);
    }

    public static NInteger nInteger(long x) {
        return new NInteger(x);
    }


    // param list

    public static NParamList paramList(boolean... xs) {
        return new NParamList(List.ofAll(xs).map(Value::nBoolean));
    }

    public static NParamList paramList(double... xs) {
        return new NParamList(List.ofAll(xs).map(Value::nFloat));
    }

    public static NParamList paramList(long... xs) {
        return new NParamList(List.ofAll(xs).map(Value::nInteger));
    }

    public static NParamList paramList(String... xs) {
        return new NParamList(List.of(xs).map(Value::nString));
    }

    public static NParamList paramList(Parameter... xs) {
        return new NParamList(List.of(xs));
    }

    public static NParamList paramList(Iterable<Parameter> xs) {
        return new NParamList(List.ofAll(xs));
    }

    // prop list

    public static NPropList propList(List<Primitive> xs) {
        return new NPropList(xs);
    }

    public static NPropList propList(Primitive... xs) {
        return propList(List.of(xs));
    }

    public static NParamMap paramMap(String k, Parameter v) {
        return new NParamMap(TreeMap.of(k, v));
    }

    public static NParamMap paramMap(Map<String, Parameter> map) {
        return new NParamMap(Util.toTreeMap(map));
    }

    public static NNull nNull() {
        return NNull.instance;
    }

    public static NString nString(String x) {
        return new NString(x);
    }
}
