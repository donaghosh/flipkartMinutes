package repository;

import entity.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderRepository implements IOrderRepository {
    private final Map<String, Order> storage = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        storage.put(order.getOrderId(), order);
    }

    @Override
    public Order findById(String id) {
        return storage.get(id);
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Order> findByPartnerId(String partnerId) {
        List<Order> orders = new ArrayList<>();
        for (Order order : storage.values()) {
            if (partnerId.equals(order.getAssignedPartnerId())) {
                orders.add(order);
            }
        }
        return orders;
    }
}

