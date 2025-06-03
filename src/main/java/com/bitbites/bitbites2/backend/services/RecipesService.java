package com.bitbites.bitbites2.backend.services;

import com.bitbites.bitbites2.backend.audit.AuditService;
import com.bitbites.bitbites2.backend.config.ConnectionProvider;
import com.bitbites.bitbites2.backend.exceptions.ConnectionException;
import com.bitbites.bitbites2.backend.groceries.GroceryItem;
import com.bitbites.bitbites2.backend.models.GroceryItemModel;
import com.bitbites.bitbites2.backend.models.GroceryListModel;
import com.bitbites.bitbites2.backend.recipes.Recipe;
import com.bitbites.bitbites2.backend.repositories.GroceryItemsRepository;
import com.bitbites.bitbites2.backend.repositories.GroceryListsRepository;
import com.bitbites.bitbites2.backend.repositories.IngredientsRepository;
import com.bitbites.bitbites2.backend.repositories.RecipesRepository;

import java.sql.Connection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

public class RecipesService extends BitBitesService<Recipe>{
    public RecipesService() {
        repository = RecipesRepository.getInstance();
    }

    @Override
    public int add(Recipe item) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            int recipeId = repository.add(connection, item);
            int listId = GroceryListsRepository.getInstance().add(connection, new GroceryListModel(
                    0,
                    item.getIngredients(),
                    recipeId,
                    0
            ));
            for (GroceryItem groceryItem: item.getIngredients().getItems().values())
            {
                IngredientsRepository.getInstance().add(connection, groceryItem.ingredient());
                GroceryItemsRepository.getInstance().add(connection, new GroceryItemModel(
                        0,
                        groceryItem,
                        listId
                ));
            }
            return recipeId;
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database. " + e.getMessage()));
        }
        return 0;
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

    public Recipe getById(int id) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            Recipe recipe = repository.getById(connection, id).orElse(null);
            Optional<GroceryListModel> list = GroceryListsRepository.getInstance().getByRecipeId(connection, recipe.getId());
            if (list.isPresent()) {
                recipe.setGroceryList(list.get().groceryList());
            }
            return recipe;
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
            return null;
        }
    }

    public List<Recipe> getAll() {
        try (Connection connection = ConnectionProvider.getConnection()) {
            List<Recipe> recipes = repository.getAll(connection).orElse(List.of());
            for (Recipe recipe: recipes) {
                Optional<GroceryListModel> list = GroceryListsRepository.getInstance().getByRecipeId(connection, recipe.getId());
                if (list.isPresent()) {
                    recipe.setGroceryList(list.get().groceryList());
                }
            }
            return recipes;
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database." + e.getMessage()));
            return List.of();
        }
    }

    public List<String> getKitchenTypes() {
        try (Connection connection = ConnectionProvider.getConnection()) {
            RecipesRepository recipesRepository = (RecipesRepository) repository;
            return recipesRepository.getKitchenTypes(connection).orElse(List.of());
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database." + e.getMessage()));
            return List.of();
        }
    }
}
