package com.fredhonorio.neu.query;

public class RefExp extends Boxed<String> implements AsString, AsAble {
    protected RefExp(String value) {
        super(value);
    }

    @Override
    public String asString() {
        return value;
    }

    // TODO: Ref.of("a").as(Ref.of("b")).as(Ref.of("c")) works, should it?
    @Override
    public RefExp as(Ref ref) {
        return AsAble.as(this, ref);
    }
}
