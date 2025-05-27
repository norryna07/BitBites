package main.java.services;

import main.java.audit.AuditService;
import main.java.config.ConnectionProvider;
import main.java.exceptions.ConnectionException;
import main.java.repositories.BitBitesRepository;

import java.sql.Connection;
import java.util.List;

public abstract class BitBitesService <T> {
    protected BitBitesRepository<T> repository;

    public void add(T item) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            repository.add(connection, item);
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
    }
    public void deleteById(int id) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            repository.deleteById(connection, id);
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
    }

    public void deleteByName(String name) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            repository.deleteByName(connection, name);
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
    }

    public void update(T item) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            repository.update(connection, item);
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
    }
    public T getById(int id) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            return repository.getById(connection, id).orElse(null);
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
            return null;
        }
    }
    public T getByName(String name) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            return repository.getByName(connection, name).orElse(null);
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
            return null;
        }
    }

    public List<T> getAll() {
        try (Connection connection = ConnectionProvider.getConnection()) {
            return repository.getAll(connection).orElse(List.of());
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
            return List.of();
        }
    }

}
