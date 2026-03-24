package entity;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Item {
    private final String itemId;
    private final String itemName;
    private int stockQuantity;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public Item(String itemId, String itemName, int stockQuantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.stockQuantity = stockQuantity;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getStockQuantity() {
        lock.readLock().lock();
        try {
            return stockQuantity;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void updateStock(int quantity) {
        lock.writeLock().lock();
        try {
            this.stockQuantity = quantity;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean hasStock(int required) {
        lock.readLock().lock();
        try {
            return stockQuantity >= required;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean reduceStock(int quantity) {
        lock.writeLock().lock();
        try {
            if (stockQuantity >= quantity) {
                this.stockQuantity -= quantity;
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void increaseStock(int quantity) {
        lock.writeLock().lock();
        try {
            this.stockQuantity += quantity;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
