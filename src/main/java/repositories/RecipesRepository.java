package main.java.repositories;

import main.java.audit.AuditService;
import main.java.exceptions.RecipeException;
import main.java.models.GroceryListModel;
import main.java.recipes.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipesRepository implements BitBitesRepository<Recipe>{
    private static RecipesRepository instance = null;
    public static RecipesRepository getInstance() {
        if (instance == null) {
            instance = new RecipesRepository();
        }
        return instance;
    }
    private RecipesRepository() {}

    @Override
    public int add(Connection connection, Recipe item) {
        String sql = """
                INSERT INTO recipes 
                (name, category_food, kitchen_type, kilocalories, servings, instructions, duration)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getCategoryFood());
            ps.setString(3, item.getKitchenType());
            ps.setDouble(4, item.getKilocalories());
            ps.setLong(5, item.getServings());
            ps.setURL(6, item.getInstructions());
            ps.setObject(7, item.getDuration().toString(), Types.OTHER);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                {
                    AuditService.getInstance().logInsert("recipes", rs.getInt("id"));
                    return rs.getInt("id");
                } else {
                    AuditService.getInstance().logException(new RecipeException("Cannot add recipe to database."));
                }
            }

        } catch (SQLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot add recipe to database."));
        }
        return 0;
    }

    @Override
    public Optional<Recipe> getById(Connection connection, int id) {
        String sql = """
                SELECT name, category_food, kitchen_type, kilocalories, servings, instructions, duration, grocery_lists.id
                FROM recipes JOIN grocery_lists ON recipes.id = grocery_lists.recipe_id
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Optional<GroceryListModel> list = GroceryListsRepository.getInstance().getById(
                            connection,
                            rs.getInt("grocery_lists.id")
                    );
                    if (list.isEmpty()) {
                        AuditService.getInstance().logException(new RecipeException("Cannot get recipe from database."));
                        return Optional.empty();
                    }
                    Recipe recipe = RecipeFactory.createRecipe(
                            id,
                            rs.getString("name"),
                            rs.getString("category_food"),
                            rs.getString("kitchen_type"),
                            rs.getURL("instructions"),
                            rs.getDouble("kilocalories"),
                            rs.getInt("servings"),
                            list.get().groceryList()
                    );
                    return Optional.of(recipe);
                } else {
                    AuditService.getInstance().logException(new RecipeException("Cannot get recipe from database."));
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot get recipe from database."));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Recipe> getByName(Connection connection, String name) {
        String sql = """
                SELECT id, category_food, kitchen_type, kilocalories, servings, instructions, duration, grocery_lists.id
                FROM recipes JOIN grocery_lists ON recipes.id = grocery_lists.recipe_id
                WHERE name = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);

            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Optional<GroceryListModel> list = GroceryListsRepository.getInstance().getById(
                            connection,
                            rs.getInt("grocery_lists.id")
                    );
                    if (list.isEmpty()) {
                        AuditService.getInstance().logException(new RecipeException("Cannot get recipe from database."));
                        return Optional.empty();
                    }
                    Recipe recipe = RecipeFactory.createRecipe(
                            rs.getInt("id"),
                            name,
                            rs.getString("category_food"),
                            rs.getString("kitchen_type"),
                            rs.getURL("instructions"),
                            rs.getDouble("kilocalories"),
                            rs.getInt("servings"),
                            list.get().groceryList()
                    );
                    return Optional.of(recipe);
                } else {
                    AuditService.getInstance().logException(new RecipeException("Cannot get recipe from database."));
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot get recipe from database."));
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(Connection connection, int id) {
        String sql = """
                DELETE FROM recipes
                WHERE id = ?
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            int deletedRows = ps.executeUpdate();
            if (deletedRows == 0) {
                AuditService.getInstance().logException(new RecipeException("Cannot delete recipe from database."));
            } else {
                AuditService.getInstance().logDelete("recipes", id);
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot delete recipe from database."));
        }
    }

    @Override
    public void deleteByName(Connection connection, String name) {
        String sql = """
                DELETE FROM recipes
                WHERE name = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);

            int deletedRows = ps.executeUpdate();
            if (deletedRows == 0) {
                AuditService.getInstance().logException(new RecipeException("Cannot delete recipe from database."));
            } else {
                AuditService.getInstance().logDelete("recipes", name);
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot delete recipe from database."));
        }
    }

    @Override
    public void update(Connection connection, Recipe item) {
        String sql = """
                UPDATE recipes
                SET name = ?, category_food = ?, kitchen_type = ?, kilocalories = ?, servings = ?, instructions = ?, duration = ?
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getCategoryFood());
            ps.setString(3, item.getKitchenType());
            ps.setDouble(4, item.getKilocalories());
            ps.setInt(5, item.getServings());
            ps.setURL(6, item.getInstructions());
            ps.setObject(7, item.getDuration().toString(), Types.OTHER);
            ps.setInt(8, item.getId());

            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                AuditService.getInstance().logException(new RecipeException("Cannot update recipe in database."));
            } else {
                AuditService.getInstance().logUpdate("recipes", item.getId());
            }

        } catch (SQLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot update recipe in database."));
        }
    }

    @Override
    public Optional<List<Recipe>> getAll(Connection connection) {
        String sql = """
                SELECT id, name, category_food, kitchen_type, kilocalories, servings, instructions, duration, grocery_lists.id
                FROM recipes JOIN grocery_lists ON recipes.id = grocery_lists.recipe_id
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            List<Recipe> list = new ArrayList<>();
            while (rs.next()) {
                list.add(RecipeFactory.createRecipe(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category_food"),
                        rs.getString("kitchen_type"),
                        rs.getURL("instructions"),
                        rs.getDouble("kilocalories"),
                        rs.getInt("servings"),
                        null
                ));
            }
            return Optional.of(list);
        } catch (SQLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot get all recipes from database."));
            return Optional.empty();
        }
    }
}
