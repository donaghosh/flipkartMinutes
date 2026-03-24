import entity.DeliveryPartner;
import entity.Order;
import fulfillmentmanagement.FulfillmentService;
import observer.OrderEventListener;
import observer.OrderEventPublisher;
import observer.OrderNotificationListener;
import ordermanagement.OrderManagementService;
import repository.*;
import strategy.DeliveryCountBasedRankings;
import strategy.RankingStrategy;
import strategy.RatingsBasedRankings;
import strategy.RoundRobinAssignmentStrategy;
import usermanagement.UserManagementService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ICustomerRepository customerRepo = new CustomerRepository();
        IDeliveryPartnerRepository partnerRepo = new DeliveryPartnerRepository();
        IItemRepository itemRepo = new ItemRepository();
        IOrderRepository orderRepo = new OrderRepository();
        OrderEventPublisher eventPublisher = new OrderEventPublisher();
        OrderEventListener listener = new OrderNotificationListener();
        eventPublisher.addListener(listener);

        UserManagementService userService = new UserManagementService(customerRepo, partnerRepo, orderRepo);
        OrderManagementService orderService = new OrderManagementService(itemRepo, orderRepo, eventPublisher);
        FulfillmentService fulfillmentService = new FulfillmentService(orderRepo, partnerRepo, eventPublisher);

        orderService.setFulfillmentService(fulfillmentService);
        fulfillmentService.setAssignmentStrategy(new RoundRobinAssignmentStrategy());

        userService.onboardCustomer("C1", "Alice");
        userService.onboardCustomer("C2", "Bob");
        System.out.println("Onboarded customers: C1, C2");

        userService.onboardDeliveryPartner("DP1", "Ravi");
        userService.onboardDeliveryPartner("DP2", "Suresh");
        System.out.println("Onboarded partners: DP1, DP2");

        orderService.addItem("I1", "Milk", 10);
        orderService.addItem("I2", "Bread", 20);
        System.out.println("Added items: Milk(10), Bread(20)");

        String orderId1 = orderService.createOrder("C1", "Milk", 2);
        System.out.println("Created order: " + orderId1);

        Order order1 = orderService.showOrderStatus(orderId1);
        System.out.println("Order " + orderId1 + " status: " + order1.getStatus() + ", assigned to: " + order1.getAssignedPartnerId());

        String orderId2 = orderService.createOrder("C2", "Bread", 1);
        System.out.println("Created order: " + orderId2);

        Order order2 = orderService.showOrderStatus(orderId2);
        System.out.println("Order " + orderId2 + " status: " + order2.getStatus() + ", assigned to: " + order2.getAssignedPartnerId());

        String orderId3 = orderService.createOrder("C1", "Milk", 1);
        System.out.println("Created order: " + orderId3 + " (should be queued - no available partners)");

        Order order3 = orderService.showOrderStatus(orderId3);
        System.out.println("Order " + orderId3 + " assigned partner: " + (order3.getAssignedPartnerId() != null ? order3.getAssignedPartnerId() : "QUEUED"));

        fulfillmentService.pickupOrder("DP1", orderId1);
        System.out.println("Order " + orderId1 + " picked up by DP1");



        fulfillmentService.completeOrder("DP1", orderId1);
        System.out.println("Order " + orderId1 + " completed by DP1");

        userService.ratePartner("DP1",3.0, "C1", orderId1);
        DeliveryPartner dp = userService.getPartner("DP1");
        System.out.println("DeliveryPartner: " + dp.getRating());

        order3 = orderService.showOrderStatus(orderId3);
        System.out.println("Order " + orderId3 + " now assigned to: " + order3.getAssignedPartnerId() + " (auto-assigned from queue)");

        DeliveryPartner partner1 = fulfillmentService.showDeliveryPartnerStatus("DP1");
        System.out.println("Partner DP1 status: " + partner1.getStatus() + ", completed orders: " + partner1.getCompletedOrdersCount());

        DeliveryPartner partner2 = fulfillmentService.showDeliveryPartnerStatus("DP2");
        System.out.println("Partner DP2 status: " + partner2.getStatus() + ", current order: " + partner2.getCurrentOrderId());

        RankingStrategy rankingStrategy1 = new DeliveryCountBasedRankings();
        userService.setRankingStrategy(rankingStrategy1);
        List<String> dpdIds1 = userService.getTopDeliveryPartners();

        RankingStrategy rankingStrategy2 = new RatingsBasedRankings();
        userService.setRankingStrategy(rankingStrategy2);
        List<String> dpdIds2 = userService.getTopDeliveryPartners();

    }
}
