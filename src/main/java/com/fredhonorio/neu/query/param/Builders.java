package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.Exp;

public abstract class Builders {
    public static Builder Match(Path... paths) {
        return Builder.builder().Match(paths);
    }

    public Builder Return(Exp... exps) {
        return Builder.builder().Return(exps);
    }

    public Builder Create(Path... paths) {
        return Builder.builder().Create(paths);
    }

    public Builder Merge(Path path) {
        return Builder.builder().Merge(path);
    }
}
