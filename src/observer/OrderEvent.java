package observer;

import entity.Order;
import enums.OrderStatus;

public class OrderEvent {
    private final Order order;
    private final OrderStatus previousStatus;
    private final OrderStatus newStatus;

    public OrderEvent(Order order, OrderStatus previousStatus, OrderStatus newStatus) {
        this.order = order;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }

    public Order getOrder() {
        return order;
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

}
