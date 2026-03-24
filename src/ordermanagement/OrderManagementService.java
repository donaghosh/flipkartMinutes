package ordermanagement;

import entity.DeliveryPartner;
import entity.Item;
import entity.Order;
import enums.OrderStatus;
import exception.InsufficientStockException;
import exception.InvalidRequestException;
import exception.NotFoundException;
import factory.EntityFactory;
import fulfillmentmanagement.IFulfillmentService;
import observer.OrderEvent;
import observer.OrderEventPublisher;
import repository.IItemRepository;
import repository.IOrderRepository;

import java.util.concurrent.atomic.AtomicInteger;

public class OrderManagementService implements IOrderManagementService {
    private final IItemRepository itemRepository;
    private final IOrderRepository orderRepository;
    private final AtomicInteger orderCounter = new AtomicInteger(1);
    private final OrderEventPublisher eventPublisher;
    private IFulfillmentService fulfillmentService;

    public OrderManagementService(IItemRepository itemRepository, IOrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void setFulfillmentService(IFulfillmentService fulfillmentService) {
        this.fulfillmentService = fulfillmentService;
    }

    @Override
    public void addItem(String itemId, String itemName, int stockQuantity) {
        Item item = EntityFactory.createItem(itemId, itemName, stockQuantity);
        itemRepository.save(item);
    }

    @Override
    public synchronized void updateItemStock(String itemId, int stockQuantity) {
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new NotFoundException("Item not found: " + itemId);
        }
        item.updateStock(stockQuantity);
        itemRepository.save(item);
    }

    @Override
    public synchronized String createOrder(String customerId, String itemName, int itemQuantity) {
        Item item = itemRepository.findByName(itemName);
        if (item == null) {
            throw new NotFoundException("Item not found: " + itemName);
        }

        String orderId = "ORD" + orderCounter.getAndIncrement();
        Order order = EntityFactory.createOrder(orderId, customerId, itemName, itemQuantity);

        if (!item.reduceStock(itemQuantity)) {
            throw new InsufficientStockException("Insufficient stock for " + itemName);
        }

        itemRepository.save(item);
        orderRepository.save(order);

        eventPublisher.notifyListeners(new OrderEvent(order, null, OrderStatus.CREATED));

        if (fulfillmentService != null) {
            fulfillmentService.autoAssignOrder(orderId);
        }

        return orderId;
    }

    @Override
    public synchronized void cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found: " + orderId);
        }

        OrderStatus previousStatus = order.getStatus();
        if (previousStatus != OrderStatus.CREATED && previousStatus != OrderStatus.ASSIGNED) {
            throw new IllegalStateException("Cannot cancel order that has been picked up or completed");
        }

        Item item = itemRepository.findByName(order.getItemName());
        if (item != null) {
            item.increaseStock(order.getItemQuantity());
            itemRepository.save(item);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        eventPublisher.notifyListeners(new OrderEvent(order, previousStatus, OrderStatus.CANCELLED));

        if (fulfillmentService != null) {
            fulfillmentService.handleOrderCancellation(orderId);
        }
    }

    @Override
    public Order showOrderStatus(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found: " + orderId);
        }
        return order;
    }

    @Override
    public void rateOrder(String orderId, Integer rating) {
        if (orderId == null || rating == null) {
            throw new InvalidRequestException("orderId or rating is null");
        }
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Order does not exists for orderId" + orderId);
        }
        order.setRating(rating);
        orderRepository.save(order);

        // usemanagementService.updateDpRating(order.getAssignId, rating);
    }
}
