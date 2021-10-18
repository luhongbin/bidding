package org.linlinjava.litemall.admin.dto;

import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;

public class OrderGoodsinone {
    LitemallOrder Order;
    LitemallOrderGoods[] OrderGoods;

    public LitemallOrder getOrder() {
        return Order;
    }

    public void setOrder(LitemallOrder order) {
        this.Order = order;
    }

    public LitemallOrderGoods[] getOrderGoods() {
        return OrderGoods;
    }

    public void setOrderGoods(LitemallOrderGoods[] OrderGoods) {
        this.OrderGoods = OrderGoods;
    }

}
