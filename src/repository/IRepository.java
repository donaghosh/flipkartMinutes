package repository;

import java.util.List;

public interface IRepository<T> {
    void save(T entity);
    T findById(String id);
    List<T> findAll();
}
