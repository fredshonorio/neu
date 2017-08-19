package com.fredhonorio.neu.query;

import static com.fredhonorio.neu.util.Strings.concat;

public interface AsAble { // This might disappear and be just a default method in AsString, too soon to say
    RefExp as(Ref ref);

    public static RefExp as(AsString a, Ref r) {
        return new RefExp(concat(a.asString(), " as ", r.asString()));
    }
}
