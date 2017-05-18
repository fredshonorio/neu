package com.fredhonorio.neu.model;

import com.fredhonorio.neu.type.Property;
import javaslang.collection.Map;

public class Relationship {

    public final String type;
    public final Map<String, Property> properties;

    public Relationship(String type, Map<String, Property> properties) {
        this.type = type;
        this.properties = properties;
    }
}
