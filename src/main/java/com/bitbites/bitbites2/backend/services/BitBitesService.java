package com.bitbites.bitbites2.backend.services;

import com.bitbites.bitbites2.backend.audit.AuditService;
import com.bitbites.bitbites2.backend.config.ConnectionProvider;
import com.bitbites.bitbites2.backend.exceptions.ConnectionException;
import com.bitbites.bitbites2.backend.repositories.BitBitesRepository;

import java.sql.Connection;
import java.util.List;

public abstract class BitBitesService <T> {
    protected BitBitesRepository<T> repository;

    public int add(T item) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            return repository.add(connection, item);
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
        return 0;
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
