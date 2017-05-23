package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.Statement;
import com.fredhonorio.neu.type.Value;
import javaslang.collection.List;

import java.util.function.Function;

import static com.fredhonorio.neu.type.Value.paramList;

public class Builder implements Fragments {

    final List<Fragment> fragments;

    public Builder(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public static StrB of(String s) {
        return new EmptyB().s(s);
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

    public static class ParamB extends Builder implements Next.Str {

        public ParamB(List<Fragment> fragments) {
            super(fragments);
        }
    }

    public static void main(String[] args) {

        Statement s = Builder.of("MATCH")
            .node("n").to("r").node("a").from("a").node("a")
            .s("WHERE n.id IN ").param(paramList("1", "2", "3"))
            .s(" RETURN n")
            .build();

        System.out.println(s);
    }
}
