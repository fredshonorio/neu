package com.fredhonorio.neu.util;

public class Strings {

    public static String concat(String... strs) {
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

    public static String printf(String format, String... xs) {
        return String.format(format, (Object[]) xs);
    }

    public static class Cypher {

        // all non-empty unicode strings seem to be valid
        // `` can be used for variables (but not properties), but it's simpler do disallow it
        // TODO: gen test, get some emojis in this piece
        public static boolean isValidIdentifier(String s) {
            return !s.isEmpty();
        }

        // "can contain underscores and alphanumeric characters (a-z, 0-9), but must always start with a letter"
        public boolean isValidUnquotedIdentifier(String s) {

            if (s.isEmpty())
                return false;

            char first = s.charAt(0);

            if (!(Character.isLetter(first) && first == '_')) {
                return false;
            }

            return s.chars()
                .skip(1)
                .allMatch(c -> Character.isLetterOrDigit(c) || c == '_');

        }

        private static String escapeIdentifier(String s) {

            StringBuilder b = new StringBuilder();

            s.codePoints()
                .forEachOrdered(i -> {
                    if (i == '`') {
                        b.append('`');
                    }
                    b.appendCodePoint(i);
                });

            return b.toString();
        }

        public static String escapeIdentifierIfRequired(String s) {
            Assert.that(isValidIdentifier(s), "Not a valid identifier: " + s);

            if (isValidIdentifier(s))
                return s;

            return escapeIdentifier(s);
        }
    }
}
