package main.java.services;

import main.java.audit.AuditService;
import main.java.config.ConnectionProvider;
import main.java.exceptions.ConnectionException;
import main.java.models.GroceryItemModel;
import main.java.repositories.GroceryItemsRepository;
import main.java.repositories.IngredientsRepository;

import java.sql.Connection;

public class GroceryItemsService extends BitBitesService<GroceryItemModel> {
    public GroceryItemsService() {
        repository = GroceryItemsRepository.getInstance();
    }

    @Override
    public void add(GroceryItemModel item) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            IngredientsRepository.getInstance().add(connection, item.groceryItem().ingredient());
            repository.add(connection, item);
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
    }
}
