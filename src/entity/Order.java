package entity;

import enums.OrderStatus;

public class Order {
    private final String orderId;
    private final String customerId;
    private final String itemName;
    private final int itemQuantity;
    private OrderStatus status;
    private String assignedPartnerId;
    private Integer rating;

    public Order(String orderId, String customerId, String itemName, int itemQuantity) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.status = OrderStatus.CREATED;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getAssignedPartnerId() {
        return assignedPartnerId;
    }

    public void assignPartner(String partnerId) {
        this.assignedPartnerId = partnerId;
        this.status = OrderStatus.ASSIGNED;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
