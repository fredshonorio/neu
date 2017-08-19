package com.fredhonorio.neu.query;

import com.fredhonorio.neu.util.Strings;

public class RefProp implements AsString, AsAble {
    public final Ref ref;
    public final String prop;

    public RefProp(Ref ref, String prop) {
        // TODO: sanitize prop?
        this.ref = ref;
        this.prop = prop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RefProp refProp = (RefProp) o;

        if (ref != null ? !ref.equals(refProp.ref) : refProp.ref != null) return false;
        return prop != null ? prop.equals(refProp.prop) : refProp.prop == null;
    }

    @Override
    public int hashCode() {
        int result = ref != null ? ref.hashCode() : 0;
        result = 31 * result + (prop != null ? prop.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RefProp{" +
            "ref=" + ref +
            ", prop='" + prop + '\'' +
            '}';
    }


    @Override
    public String asString() {
        return Strings.concat(ref.asString(), ".", prop);
    }

    @Override
    public RefExp as(Ref ref) {
        return AsAble.as(this, ref);
    }
}
