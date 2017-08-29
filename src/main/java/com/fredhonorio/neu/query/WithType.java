package com.fredhonorio.neu.query;

import com.fredhonorio.neu.type.Type;

import java.util.function.Supplier;

/**
 * A value that has (or can be converted to) a {@link com.fredhonorio.neu.type.Relationship} {@link com.fredhonorio.neu.type.Type}
 */
public interface WithType extends Supplier<Type> {
    Type type();

    @Override
    default Type get() {
        return type();
    }
}
