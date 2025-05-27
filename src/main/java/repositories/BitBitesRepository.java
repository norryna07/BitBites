package main.java.repositories;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface BitBitesRepository <T> {
    int add(Connection connection, T item);
    Optional<T> getById(Connection connection, int id);
    Optional<T> getByName(Connection connection, String name);
    void deleteById(Connection connection, int id);
    void deleteByName(Connection connection, String name);
    void update(Connection connection, T item);
    Optional<List<T>> getAll(Connection connection);
}
