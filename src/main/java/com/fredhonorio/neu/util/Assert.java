package com.fredhonorio.neu.util;

public class Assert {

    public static void that(boolean test, String message) {
        if (!test)
            throw new AssertionError(message);
    }
}
