package fulfillmentmanagement;

import entity.DeliveryPartner;
import strategy.OrderAssignmentStrategy;

public interface IFulfillmentService {
    void setAssignmentStrategy(OrderAssignmentStrategy strategy);
    void autoAssignOrder(String orderId);
    void handleOrderCancellation(String orderId);
    void pickupOrder(String partnerId, String orderId);
    void completeOrder(String partnerId, String orderId);
    DeliveryPartner showDeliveryPartnerStatus(String partnerId);
}
