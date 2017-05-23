package com.fredhonorio.neu.query;

import com.fredhonorio.neu.type.Label;
import com.fredhonorio.neu.type.Node;
import com.fredhonorio.neu.type.Property;
import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.TreeMap;
import com.fredhonorio.neu.type.NParamMap;

public class Statement {

    public final String queryTemplate;
    public final NParamMap params;

    public Statement(String queryTemplate, NParamMap params) {
        this.queryTemplate = queryTemplate;
        this.params = params;
    }

    }

    public static Statement of(String queryTemplate) {
        return new Statement(queryTemplate, NParamMap.empty());
    }
}
