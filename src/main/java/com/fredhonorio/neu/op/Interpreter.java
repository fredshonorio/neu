package com.fredhonorio.neu.op;

import javaslang.control.Try;

public interface Interpreter {
    <T> Try<T> submit(GraphDB<T> op);
}
