package com.fredhonorio.neu.query;

import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.Type;

import java.util.function.Supplier;

public interface ToLabel extends Supplier<Label> {
    Label label();

    @Override
    default Label get() {
        return label();
    }
}
