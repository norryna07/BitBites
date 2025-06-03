package com.bitbites.bitbites2.backend.repositories;

import com.bitbites.bitbites2.backend.audit.AuditService;
import com.bitbites.bitbites2.backend.exceptions.RecipeException;
import com.bitbites.bitbites2.backend.models.GroceryListModel;
import com.bitbites.bitbites2.backend.recipes.*;

import javax.swing.text.html.Option;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.Duration;

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
                VALUES (?, ?::food_type, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getCategoryFood());
            ps.setString(3, item.getKitchenType());
            ps.setDouble(4, item.getKilocalories());
            ps.setLong(5, item.getServings());
            ps.setString(6, item.getInstructions().toString());
            ps.setString(7, item.getDuration().toString());

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
            AuditService.getInstance().logException(new RecipeException("Cannot add recipe to database. " + e.getMessage()));
        }
        return 0;
    }

    @Override
    public Optional<Recipe> getById(Connection connection, int id) {
        String sql = """
                SELECT name, category_food, kitchen_type, kilocalories, servings, instructions, duration, grocery_lists.id
                FROM recipes JOIN grocery_lists ON recipes.id = grocery_lists.recipe_id
                WHERE recipes.id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Optional<GroceryListModel> list = GroceryListsRepository.getInstance().getById(
                            connection,
                            rs.getInt("id")
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
                            new URL(rs.getString("instructions")),
                            rs.getDouble("kilocalories"),
                            rs.getInt("servings"),
                            list.get().groceryList()
                    );
                    recipe.setDuration(Duration.parse(rs.getString("duration")));
                    return Optional.of(recipe);
                } else {
                    AuditService.getInstance().logException(new RecipeException("Cannot get recipe from database."));
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot get recipe from database." + e.getMessage()));
        } catch (MalformedURLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot get recipe from database. Invalid URL. "));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Recipe> getByName(Connection connection, String name) {
        String sql = """
                SELECT recipes.id, category_food, kitchen_type, kilocalories, servings, instructions, duration, grocery_lists.id
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
                    recipe.setDuration(Duration.parse(rs.getString("duration")));
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
                SELECT recipes.id, name, category_food, kitchen_type, kilocalories, servings, instructions, duration, grocery_lists.id
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
                        new URL(rs.getString("instructions")),
                        rs.getDouble("kilocalories"),
                        rs.getInt("servings"),
                        null
                ));
                list.get(list.size() - 1).setDuration(Duration.parse(rs.getString("duration")));
            }
            return Optional.of(list);
        } catch (SQLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot get all recipes from database." + e.getMessage()));
            return Optional.empty();
        } catch (MalformedURLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot get recipe from database."));
            return Optional.empty();
        }
    }

    public Optional<List<Recipe>> getByMealPlanId(Connection connection, int mealPlanId) {
        String sql = """
                SELECT recipes.id, name, category_food, kitchen_type, kilocalories, servings, instructions, duration, grocery_lists.id
                FROM recipes JOIN grocery_lists ON recipes.id = grocery_lists.recipe_id
                JOIN self_plan_recipes ON recipes.id = self_plan_recipes.recipe_id
                WHERE meal_plan_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, mealPlanId);
            try (ResultSet rs = ps.executeQuery()) {
            List<Recipe> list = new ArrayList<>();
            while (rs.next()) {
                list.add(RecipeFactory.createRecipe(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category_food"),
                        rs.getString("kitchen_type"),
                        new URL(rs.getString("instructions")),
                        rs.getDouble("kilocalories"),
                        rs.getInt("servings"),
                        null
                ));
                list.getLast().setDuration(Duration.parse(rs.getString("duration")));
                AuditService.getInstance().logUpdate("recipes", mealPlanId);
            }
            return Optional.of(list);
        }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot get all recipes from database." + e.getMessage()));
            return Optional.empty();
        } catch (MalformedURLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot get recipe from database."));
            return Optional.empty();
        }
    }

    public Optional<List<String>> getKitchenTypes(Connection connection) {
        String sql = """
                SELECT DISTINCT kitchen_type
                FROM recipes
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<String> kitchenTypes = new ArrayList<>();
            while (rs.next()) {
                kitchenTypes.add(rs.getString("kitchen_type"));
            }
            return Optional.of(kitchenTypes);
        } catch (SQLException e) {
            AuditService.getInstance().logException(new RecipeException("Cannot get kitchen types from database."));
        }
        return Optional.empty();
    }
}
