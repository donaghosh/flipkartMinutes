package repository;

import entity.Order;

import java.util.List;

public interface IOrderRepository extends IRepository<Order> {
    List<Order> findByPartnerId(String partnerId);
}
