package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.Statement;
import javaslang.collection.List;

import static com.fredhonorio.neu.type.Value.paramList;

public class Builder implements Fragments {

    final List<Fragment> fragments;

    public Builder(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public static EmptyB builder() {
        return new EmptyB();
    }

    @Override
    public List<Fragment> fragments() {
        return fragments;
    }

    public static class EmptyB extends Builder implements Next.Str {
        public EmptyB() {
            super(List.empty());
        }
    }

    public static class StrB extends Builder implements Next.Str, Next.Node, Next.Param, Next.Final {
        public StrB(List<Fragment> fragments) {
            super(fragments);
        }
    }

    public static class NodeB extends Builder implements Next.Str, Next.To, Next.From, Next.Final {
        public NodeB(List<Fragment> fragments) {
            super(fragments);
        }
    }

    public static class ToB extends Builder implements Next.Node {
        public ToB(List<Fragment> fragments) {
            super(fragments);
        }
    }

    public static class FromB extends Builder implements Next.Node {
        public FromB(List<Fragment> fragments) {
            super(fragments);
        }
    }

    public static class ParamB extends Builder implements Next.Str, Next.Final {

        public ParamB(List<Fragment> fragments) {
            super(fragments);
        }
    }
}
