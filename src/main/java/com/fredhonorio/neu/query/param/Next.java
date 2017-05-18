package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.type.NParamMap;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.control.Option;
import com.fredhonorio.neu.type.Properties;
import com.fredhonorio.neu.query.Statement;

public interface Next {

    interface Str extends Fragments {
        default Builder.StrB s(String s) {
            return new Builder.StrB(fragments().append(new Fragment.Str(s)));
        }
    }

    interface Node extends Fragments {
        default Builder.NodeB node(String name) {
            return new Builder.NodeB(fragments().append(new Fragment.Node(name, HashSet.empty(), Properties.empty())));
        }
    }

    interface From extends Fragments {
        default Builder.FromB from(String s) {
            return new Builder.FromB(fragments().append(new Fragment.Rel(Fragment.Dir.FROM, Option.some(s), Option.none())));
        }
    }

    interface To extends Fragments {
        default Builder.ToB to(String s) {
            return new Builder.ToB(fragments().append(new Fragment.Rel(Fragment.Dir.TO, Option.some(s), Option.none())));
        }
    }

    interface Final extends Fragments {
        default Statement build() {

            List<Fragment> fragments = fragments();

            String query = fragments
                .foldLeft(
                    "",
                    (z, x) -> x.match(
                        str -> str.s,
                        Fragment.Node::pattern,
                        Fragment.Rel::pattern
                    )
                );

            return new Statement(query, NParamMap.empty());
        }

        default String show() {
            return null;
        }
    }
}
