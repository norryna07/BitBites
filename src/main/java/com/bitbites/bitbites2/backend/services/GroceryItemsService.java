package com.bitbites.bitbites2.backend.services;

import com.bitbites.bitbites2.backend.audit.AuditService;
import com.bitbites.bitbites2.backend.config.ConnectionProvider;
import com.bitbites.bitbites2.backend.exceptions.ConnectionException;
import com.bitbites.bitbites2.backend.models.GroceryItemModel;
import com.bitbites.bitbites2.backend.repositories.GroceryItemsRepository;
import com.bitbites.bitbites2.backend.repositories.IngredientsRepository;

import java.sql.Connection;

public class GroceryItemsService extends BitBitesService<GroceryItemModel> {
    IngredientsRepository ingredientsRepository;
    public GroceryItemsService() {
        repository = GroceryItemsRepository.getInstance();
        ingredientsRepository = IngredientsRepository.getInstance();
    }

    @Override
    public int add(GroceryItemModel item) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            assert connection != null;
            ingredientsRepository.add(connection, item.groceryItem().ingredient());
            return repository.add(connection, item);
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
        return 0;
    }
}
