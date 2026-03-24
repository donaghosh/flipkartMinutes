package ordermanagement;

import entity.Order;
import fulfillmentmanagement.IFulfillmentService;

public interface IOrderManagementService {
    void addItem(String itemId, String itemName, int stockQuantity);
    void updateItemStock(String itemId, int stockQuantity);
    String createOrder(String customerId, String itemName, int itemQuantity);
    void cancelOrder(String orderId);
    Order showOrderStatus(String orderId);
    void setFulfillmentService(IFulfillmentService fulfillmentService);
    void rateOrder(String dp1, Integer v);
}
