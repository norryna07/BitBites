package com.bitbites.bitbites2.backend.services;

import com.bitbites.bitbites2.backend.audit.AuditService;
import com.bitbites.bitbites2.backend.config.ConnectionProvider;
import com.bitbites.bitbites2.backend.exceptions.ConnectionException;
import com.bitbites.bitbites2.backend.groceries.GroceryItem;
import com.bitbites.bitbites2.backend.models.GroceryItemModel;
import com.bitbites.bitbites2.backend.models.GroceryListModel;
import com.bitbites.bitbites2.backend.repositories.GroceryItemsRepository;
import com.bitbites.bitbites2.backend.repositories.GroceryListsRepository;
import com.bitbites.bitbites2.backend.repositories.IngredientsRepository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class GroceryListsService extends BitBitesService<GroceryListModel>{
    private GroceryItemsRepository itemsRepository;
    private IngredientsRepository ingredientsRepository;
    public GroceryListsService() {
        repository = GroceryListsRepository.getInstance();
        itemsRepository = GroceryItemsRepository.getInstance();
        ingredientsRepository = IngredientsRepository.getInstance();
    }

    @Override
    public int add(GroceryListModel item) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            int id = repository.add(connection, item);
            for (GroceryItem groceryItem: item.groceryList().getItems().values())
            {
                ingredientsRepository.add(connection, groceryItem.ingredient());
                itemsRepository.add(connection, new GroceryItemModel(
                        0,
                        groceryItem,
                        id
                ));
            }
            return id;
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
        return 0;
    }

    @Override
    public void update(GroceryListModel item) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            repository.update(connection, item);
            Optional<List<GroceryItemModel>> oldItems = itemsRepository.getAllByListId(connection, item.id());
            if (oldItems.isPresent()) {
                for (GroceryItemModel oldItem: oldItems.get()) {
                    itemsRepository.deleteById(connection, oldItem.id());
                }
                for (GroceryItem groceryItem: item.groceryList().getItems().values())
                {
                    itemsRepository.add(connection, new GroceryItemModel(
                            0,
                            groceryItem,
                            item.id()
                    ));
                }
            }
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            Optional<List<GroceryItemModel>> oldItems = itemsRepository.getAllByListId(connection, id);
            if (oldItems.isPresent()) {
                for (GroceryItemModel oldItem: oldItems.get()) {
                    itemsRepository.deleteById(connection, oldItem.id());
                }
            }
            repository.deleteById(connection, id);
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
    }
}
