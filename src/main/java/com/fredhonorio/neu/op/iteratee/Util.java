package com.fredhonorio.neu.op.iteratee;

public class Util {

    public static <T> T fail(String msg) {
        throw new RuntimeException(msg);
    }
}
