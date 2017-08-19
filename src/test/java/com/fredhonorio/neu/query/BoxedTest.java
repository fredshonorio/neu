package com.fredhonorio.neu.query;

import org.junit.Test;

import static org.junit.Assert.*;

public class BoxedTest {

    public static class X extends Boxed<String> {
        protected X(String value) {
            super(value);
        }
    }

    @Test
    public void toStr() {
        assertEquals("X{x}", new X("x").toString());
    }

}