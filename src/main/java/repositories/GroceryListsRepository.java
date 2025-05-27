package main.java.repositories;

import main.java.audit.AuditService;
import main.java.exceptions.GroceryItemException;
import main.java.exceptions.GroceryListException;
import main.java.groceries.GroceryItem;
import main.java.groceries.GroceryList;
import main.java.groceries.Ingredient;
import main.java.groceries.Unit;
import main.java.models.GroceryListModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroceryListsRepository implements BitBitesRepository<GroceryListModel>{
    private static GroceryListsRepository instance = null;
    public static GroceryListsRepository getInstance() {
        if (instance == null) {
            instance = new GroceryListsRepository();
        }
        return instance;
    }
    private GroceryListsRepository() {}

    @Override
    public int add(Connection connection, GroceryListModel item) {
        String sql = """
                INSERT INTO grocery_lists (recipe_id, mealplan_id)
                VALUES (?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, item.recipeId());
            ps.setLong(2, item.mealPlanId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    AuditService.getInstance().logInsert("grocery_lists", rs.getInt("id"));
                    return rs.getInt("id");
                } else {
                    AuditService.getInstance().logException(new GroceryListException("Cannot add grocery list to database."));
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new GroceryListException("Cannot add grocery list to database."));
        }
        return 0;
    }

    @Override
    public Optional<GroceryListModel> getById(Connection connection, int id) {
        String sql = """
                SELECT recipe_id, mealplan_id, grocery_list_id, ingredient, category, quantity, unit
                FROM grocery_lists 
                    JOIN grocery_items ON grocery_lists.id = grocery_items.grocery_list_id
                    JOIN ingredients ON grocery_items.ingredient = ingredients.name
                WHERE grocery_lists.id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                GroceryList list = new GroceryList();
                int recipeId = 0, mealPlanId = 0;
                while (rs.next()) {
                    list.addItem(new GroceryItem(
                            rs.getDouble("quantity"),
                            Unit.valueOf(rs.getString("unit").toUpperCase()),
                            new Ingredient(
                                    rs.getString("ingredient"),
                                    rs.getString("category")
                            )
                    ));
                    recipeId = rs.getInt("recipe_id");
                    mealPlanId = rs.getInt("mealplan_id");
                }

                return Optional.of(new GroceryListModel(
                        id,
                        list,
                        recipeId,
                        mealPlanId
                ));
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new GroceryListException("Cannot get grocery list from database: " + id));
        }
        return Optional.empty();
    }

    @Override
    public Optional<GroceryListModel> getByName(Connection connection, String name) {
        AuditService.getInstance().logException(new GroceryListException("Cannot get grocery list from database by name."));
        return Optional.empty();
    }

    @Override
    public void deleteById(Connection connection, int id) {
        String sql = """
                DELETE FROM grocery_lists
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int deletedRows = ps.executeUpdate();
            if (deletedRows == 0) {
                AuditService.getInstance().logException(new GroceryListException("Cannot delete grocery list from database: " + id));
            } else {
                AuditService.getInstance().logDelete("grocery_lists", id);
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new GroceryListException("Cannot delete grocery list from database: " + id));
        }
    }

    @Override
    public void deleteByName(Connection connection, String name) {
        AuditService.getInstance().logException(new GroceryListException("Cannot delete grocery list from database by name. "));
    }

    @Override
    public void update(Connection connection, GroceryListModel item) {
        String sql = """
                UPDATE grocery_lists
                SET recipe_id = ?, mealplan_id = ?
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, item.recipeId());
            ps.setLong(2, item.mealPlanId());
            ps.setLong(3, item.id());
            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                AuditService.getInstance().logException(new GroceryItemException("Cannot update grocery list in database: " + item.id()));
            } else {
                AuditService.getInstance().logUpdate("grocery_lists", item.id());
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new GroceryListException("Cannot update grocery list in database: " + item.id()));
        }
    }

    @Override
    public Optional<List<GroceryListModel>> getAll(Connection connection) {
        String sql = """
                SELECT id, recipe_id, mealplan_id
                FROM grocery_lists
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<GroceryListModel> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new GroceryListModel(
                            rs.getInt("id"),
                            new GroceryList(),
                            rs.getInt("recipe_id"),
                            rs.getInt("mealplan_id")
                            ));
                }
                return Optional.of(list);
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new GroceryListException("Cannot get all grocery lists from database."));
        }
        return Optional.empty();
    }

    public Optional<GroceryListModel> getByRecipeId(Connection connection, int recipeId) {
        String sql = """
                SELECT id
                FROM grocery_lists
                WHERE recipe_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, recipeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    return getById(connection, rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new GroceryListException("Cannot get grocery list from database: " + recipeId));
        }
        return Optional.empty();
    }

    public Optional<GroceryListModel> getByMeanPlanId(Connection connection, int mealPlanId) {
        String sql = """
                SELECT id
                FROM grocery_lists
                WHERE mealplan_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, mealPlanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    return getById(connection, rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new GroceryListException("Cannot get grocery list from database: " + mealPlanId));
        }
        return Optional.empty();
    }
}
