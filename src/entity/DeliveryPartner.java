package entity;

import enums.PartnerStatus;

import java.util.List;

public class DeliveryPartner {
    private final String partnerId;
    private final String name;
    private PartnerStatus status;
    private String currentOrderId;
    private int completedOrdersCount;
    private double rating;
    private List<Integer> orderRatings;


    public DeliveryPartner(String partnerId, String name) {
        this.partnerId = partnerId;
        this.name = name;
        this.status = PartnerStatus.AVAILABLE;
        this.currentOrderId = null;
        this.completedOrdersCount = 0;
        this.rating = 0.0;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getName() {
        return name;
    }

    public PartnerStatus getStatus() {
        return status;
    }

    public void setStatus(PartnerStatus status) {
        this.status = status;
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    public int getCompletedOrdersCount() {
        return completedOrdersCount;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void assignOrder(String orderId) {
        this.currentOrderId = orderId;
        this.status = PartnerStatus.BUSY;
    }

    public void completeOrder() {
        this.currentOrderId = null;
        this.completedOrdersCount++;
        this.status = PartnerStatus.AVAILABLE;
    }

    public void freeUp() {
        this.currentOrderId = null;
        this.status = PartnerStatus.AVAILABLE;
    }
}
