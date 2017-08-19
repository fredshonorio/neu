package com.fredhonorio.neu.query;

import com.fredhonorio.neu.util.Strings;
import static com.fredhonorio.neu.util.Strings.concat;

public class Ref extends Boxed<String> implements AsString, AsAble {

    private Ref(String value) {
        super(value);
    }

    public static Ref of(String name) {
        return new Ref(name);
    }

    @Override
    public String asString() {
        return value;
    }

    public RefProp dot(Field f) {
        return new RefProp(this, f.name());
    }

    @Override
    public RefExp as(Ref ref) {
        return AsAble.as(this, ref);
    }
}
