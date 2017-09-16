package com.fredhonorio.neu.query;

import java.util.function.Supplier;

/**
 * A value that has (or can be converted to) a {@link Field}
 */
public interface ToField extends Supplier<Field> {
    Field field();

    @Override
    default Field get() {
        return field();
    }
}
