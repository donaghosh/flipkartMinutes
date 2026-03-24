package repository;

import entity.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemRepository implements IItemRepository {
    private final Map<String, Item> storage = new ConcurrentHashMap<>();

    @Override
    public void save(Item item) {
        storage.put(item.getItemId(), item);
    }

    @Override
    public Item findById(String id) {
        return storage.get(id);
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Item findByName(String name) {
        for (Item item : storage.values()) {
            if (item.getItemName().equals(name)) {
                return item;
            }
        }
        return null;
    }
}

