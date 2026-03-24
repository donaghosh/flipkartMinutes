package repository;

import entity.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerRepository implements ICustomerRepository {
    private final Map<String, Customer> storage = new ConcurrentHashMap<>();

    @Override
    public void save(Customer customer) {
        storage.put(customer.getCustomerId(), customer);
    }

    @Override
    public Customer findById(String id) {
        return storage.get(id);
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(storage.values());
    }
}

