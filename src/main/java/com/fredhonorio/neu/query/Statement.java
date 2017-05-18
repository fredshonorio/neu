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

    public static Statement createNode(Node node) {
        List<Label> labels = node.labels.toList();
        TreeMap<String, Property> props = node.properties.asMap();

        String b = "CREATE (n:" +
            labels.map(e -> e.value).mkString(":") +
            ' ' +
            asPropertyTemplate(props) +
            ')';

        return new Statement(
            b,
            new NParamMap(props.mapValues(Property::asParam))
        );

    }

    public static Statement of(String queryTemplate) {
        return new Statement(queryTemplate, NParamMap.empty());
    }

    private static String asPropertyTemplate(Map<String, Property> props) {
        return props.toList().map(Tuple2::_1)
            .map(p -> p + ": $" + p)
            .mkString("{", ",", "}");
    }

}
