package com.fredhonorio.neu.query.param;

import com.fredhonorio.neu.query.Exp;

public class OrderByClause {

    public static enum Order {
        asc, desc;

        public String cypher() {
            return name();
        }
    }

    public final Order order;
    public final Exp exp;

    public OrderByClause(Order order, Exp exp) {
        this.order = order;
        this.exp = exp;
    }

    public static OrderByClause desc(Exp exp) {
        return new OrderByClause(Order.desc, exp);
    }

    public static OrderByClause asc(Exp exp) {
        return new OrderByClause(Order.asc, exp);
    }
}
