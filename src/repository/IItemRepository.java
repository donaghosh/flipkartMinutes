package repository;

import entity.Item;

public interface IItemRepository extends IRepository<Item> {
    Item findByName(String name);
}
