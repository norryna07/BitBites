package main.java.services;

import main.java.audit.AuditService;
import main.java.config.ConnectionProvider;
import main.java.exceptions.ConnectionException;
import main.java.groceries.GroceryItem;
import main.java.models.GroceryItemModel;
import main.java.models.GroceryListModel;
import main.java.recipes.Recipe;
import main.java.repositories.GroceryItemsRepository;
import main.java.repositories.GroceryListsRepository;
import main.java.repositories.RecipesRepository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class RecipesService extends BitBitesService<Recipe>{
    public RecipesService() {
        repository = RecipesRepository.getInstance();
    }

    @Override
    public void add(Recipe item) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            repository.add(connection, item);
            int listId = GroceryListsRepository.getInstance().add(connection, new GroceryListModel(
                    0,
                    item.getIngredients(),
                    item.getId(),
                    0
            ));
            for (GroceryItem groceryItem: item.getIngredients().getItems().values())
            {
                GroceryItemsRepository.getInstance().add(connection, new GroceryItemModel(
                        0,
                        groceryItem,
                        listId
                ));
            }
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
    }

    public void deleteById(int id) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            Optional<GroceryListModel> list = GroceryListsRepository.getInstance().getByRecipeId(connection, id);
            if (list.isPresent()) {
                Optional<List<GroceryItemModel>> oldItems = GroceryItemsRepository.getInstance().getAllByListId(connection, list.get().id());
                if (oldItems.isPresent()) {
                    for (GroceryItemModel oldItem: oldItems.get()) {
                        GroceryItemsRepository.getInstance().deleteById(connection, oldItem.id());
                    }
                }
                GroceryListsRepository.getInstance().deleteById(connection, list.get().id());
            }
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

    public void update(Recipe item) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            repository.update(connection, item);
            Optional<GroceryListModel> list = GroceryListsRepository.getInstance().getByRecipeId(connection, item.getId());
            if (list.isPresent()) {
                Optional<List<GroceryItemModel>> oldItems = GroceryItemsRepository.getInstance().getAllByListId(connection, list.get().id());
                if (oldItems.isPresent()) {
                    for (GroceryItemModel oldItem : oldItems.get()) {
                        GroceryItemsRepository.getInstance().deleteById(connection, oldItem.id());
                    }
                    for (GroceryItem groceryItem : item.getIngredients().getItems().values()) {
                        GroceryItemsRepository.getInstance().add(connection, new GroceryItemModel(
                                0,
                                groceryItem,
                                list.get().id()
                        ));
                    }
                }
            }
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
    }

}
