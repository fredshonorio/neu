package com.fredhonorio.neu.util;

public class Strings {

    public static String concat(String...strs) {
        StringBuilder s = new StringBuilder();

        for (String str : strs) {
            s.append(str);
        }

        return s.toString();
    }

    public static String concat(Iterable<String> strs) {
        StringBuilder s = new StringBuilder();

        for (String str : strs) {
            s.append(str);
        }

        return s.toString();
    }
}
