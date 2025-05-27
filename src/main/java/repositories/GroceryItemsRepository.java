package main.java.repositories;

import main.java.audit.AuditService;
import main.java.exceptions.GroceryItemException;
import main.java.groceries.GroceryItem;
import main.java.groceries.Ingredient;
import main.java.groceries.Unit;
import main.java.models.GroceryItemModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroceryItemsRepository implements BitBitesRepository<GroceryItemModel>{
    private static GroceryItemsRepository instance = null;
    public static GroceryItemsRepository getInstance() {
        if (instance == null) {
            instance = new GroceryItemsRepository();
        }
        return instance;
    }
    private GroceryItemsRepository() {}

    @Override
    public int add(Connection connection, GroceryItemModel item) {
        String sql = """
                INSERT INTO grocery_items (ingredient, quantity, unit, grocery_list_id)
                VALUES (?, ?, ?::unit_type, ?);
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, item.groceryItem().ingredient().toString());
            ps.setDouble(2, item.groceryItem().quantity());
            ps.setString(3, item.groceryItem().unit().toString());
            ps.setInt(4, item.listId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    AuditService.getInstance().logInsert("grocery_items", rs.getInt("id"));
                    return rs.getInt("id");
                } else {
                    AuditService.getInstance().logException(new GroceryItemException("Cannot add grocery item to database."));
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new GroceryItemException("Cannot add grocery item to database."));
        }
        return 0;
    }

    @Override
    public Optional<GroceryItemModel> getById(Connection connection, int id) {
        String sql = """
                SELECT ingredient, category, quantity, unit, grocery_list_id
                FROM grocery_items JOIN ingredients ON grocery_items.ingredient = ingredients.name
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ingredient ingredient = new Ingredient(
                            rs.getString("ingredient"),
                            rs.getString("category")
                    );
                    GroceryItem groceryItem = new GroceryItem(
                            rs.getDouble("quantity"),
                            Unit.valueOf(rs.getString("unit").toUpperCase()),
                            ingredient
                    );
                    return Optional.of(new GroceryItemModel(
                            id,
                            groceryItem,
                            rs.getInt("grocery_list_id")
                    ));
                } else  {
                    AuditService.getInstance().logException(new GroceryItemException("Cannot get grocery item from database: " + id));
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new GroceryItemException("Cannot get grocery item from database: " + id));
        }
        return Optional.empty();
    }

    @Override
    public Optional<GroceryItemModel> getByName(Connection connection, String name) {
        AuditService.getInstance().logException(new GroceryItemException("Cannot get grocery item from database by name."));
        return Optional.empty();
    }

    @Override
    public void deleteById(Connection connection, int id) {
        String sql = """
                DELETE FROM grocery_items
                where id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            int deletedRows = ps.executeUpdate();
            if (deletedRows == 0) {
                AuditService.getInstance().logException(new GroceryItemException("Cannot delete grocery item from database: " + id));
            } else {
                AuditService.getInstance().logDelete("grocery_items", id);
            }

        } catch (SQLException e) {
            AuditService.getInstance().logException(new GroceryItemException("Cannot delete grocery item from database: " + id));
        }
    }

    @Override
    public void deleteByName(Connection connection, String name) {
        AuditService.getInstance().logException(new GroceryItemException("Cannot delete grocery item from database by name. "));
    }

    @Override
    public void update(Connection connection, GroceryItemModel item) {
        String sql = """
                UPDATE grocery_items
                SET quantity = ?, unit = ?::unit_type
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, item.groceryItem().quantity());
            ps.setString(2, item.groceryItem().unit().toString());
            ps.setLong(3, item.id());

            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                AuditService.getInstance().logException(new GroceryItemException("Cannot update grocery item in database: " + item.id()));
            } else {
                AuditService.getInstance().logUpdate("grocery_items", item.id());
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new GroceryItemException("Cannot update grocery item in database: " + item.id()));
        }
    }

    @Override
    public Optional<List<GroceryItemModel>> getAll(Connection connection) {
        String sql = """
                SELECT id, ingredient, category, quantity, unit, grocery_list_id
                FROM grocery_items JOIN ingredients ON grocery_items.ingredient = ingredients.name
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            List<GroceryItemModel> items = new ArrayList<>();
            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getString("ingredient"),
                        rs.getString("category")
                );
                GroceryItem groceryItem = new GroceryItem(
                        rs.getDouble("quantity"),
                        Unit.valueOf(rs.getString("unit").toUpperCase()),
                        ingredient
                );
                items.add(new GroceryItemModel(
                        rs.getInt("id"),
                        groceryItem,
                        rs.getInt("grocery_list_id")
                ));
            }
            return Optional.of(items);

        } catch (SQLException e) {
            AuditService.getInstance().logException(new GroceryItemException("Cannot get all grocery items from database."));
        }
        return Optional.empty();
    }

    public Optional<List<GroceryItemModel>> getAllByListId(Connection connection, int listId) {
        String sql = """
                SELECT id
                FROM grocery_items
                WHERE grocery_list_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, listId);
            try (ResultSet rs = ps.executeQuery()) {
                List<GroceryItemModel> list = new ArrayList<>();
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient(
                            rs.getString("ingredient"),
                            rs.getString("category")
                    );
                    GroceryItem groceryItem = new GroceryItem(
                            rs.getDouble("quantity"),
                            Unit.valueOf(rs.getString("unit").toUpperCase()),
                            ingredient
                    );
                    list.add(new GroceryItemModel(
                            rs.getInt("id"),
                            groceryItem,
                            rs.getInt("grocery_list_id")
                    ));
                    return Optional.of(list);
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new GroceryItemException("Cannot get all grocery items from database."));
        }
        return Optional.empty();
    }
}
