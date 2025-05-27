package main.java.services;

import main.java.audit.AuditService;
import main.java.config.ConnectionProvider;
import main.java.exceptions.ConnectionException;
import main.java.groceries.GroceryItem;
import main.java.groceries.GroceryList;
import main.java.models.GroceryItemModel;
import main.java.models.GroceryListModel;
import main.java.repositories.GroceryItemsRepository;
import main.java.repositories.GroceryListsRepository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class GroceryListsService extends BitBitesService<GroceryListModel>{
    private GroceryItemsRepository itemsRepository;
    public GroceryListsService() {
        repository = GroceryListsRepository.getInstance();
        itemsRepository = GroceryItemsRepository.getInstance();
    }

    @Override
    public void add(GroceryListModel item) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            int id = repository.add(connection, item);
            for (GroceryItem groceryItem: item.groceryList().getItems().values())
            {
                itemsRepository.add(connection, new GroceryItemModel(
                        0,
                        groceryItem,
                        id
                ));
            }
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
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
