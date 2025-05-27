package main.java.repositories;

import main.java.audit.AuditService;
import main.java.exceptions.IngredientException;
import main.java.groceries.Ingredient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class IngredientsRepository implements BitBitesRepository<Ingredient> {
    private static IngredientsRepository instance = null;
    private IngredientsRepository() {}

    public static IngredientsRepository getInstance() {
        if (instance == null) {
            instance = new IngredientsRepository();
        }
        return instance;
    }

    @Override
    public int add(Connection connection, Ingredient item) {
        String sql = """
                    INSERT INTO ingredients (name, category) VALUES (?, ?);
                    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, item.name());
            ps.setString(2, item.category());

            ps.executeUpdate();

            AuditService.getInstance().logInsert("ingredients", item.name());
        } catch (SQLException e) {
            AuditService.getInstance().logException(new IngredientException("Cannot add ingredient to database: " + item.name()));
        }
        return 0;
    }

    @Override
    public Optional<Ingredient> getById(Connection connection, int id) {
        AuditService.getInstance().logException(new IngredientException("Cannot get ingredient from database by id. "));
        return Optional.empty();
    }

    @Override
    public Optional<Ingredient> getByName(Connection connection, String name) {
        String sql = """
                SELECT name, category
                FROM ingredients
                where name = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1,  name);

            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next()) {
                    return Optional.of(new Ingredient(
                            rs.getString("name"),
                            rs.getString("category")
                            )
                    );
                } else {
                    AuditService.getInstance().logException(new IngredientException("Cannot get ingredient from database: " + name));
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new IngredientException("Cannot get ingredient from database: " + name));
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(Connection connection, int id) {
        AuditService.getInstance().logException(new IngredientException("Cannot delete ingredient from database by id. "));
    }

    @Override
    public void deleteByName(Connection connection, String name) {
        String sql = """
                DELETE FROM ingredients
                WHERE name = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            int deletedRows = ps.executeUpdate();
            if (deletedRows == 0) {
                AuditService.getInstance().logException(new IngredientException("Cannot delete ingredient from database: " + connection));
            }
            else {
                AuditService.getInstance().logDelete("ingredients", name);
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new IngredientException("Cannot delete ingredient from database: " + connection));
        }
    }

    @Override
    public void update(Connection connection, Ingredient item) {
        String sql = """
                UPDATE ingredients
                SET category = ?
                WHERE name = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, item.category());
            ps.setString(2, item.name());
            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                AuditService.getInstance().logException(new IngredientException("Cannot update ingredient in database: " + item.name()));
            } else {
                AuditService.getInstance().logUpdate("ingredients", item.name());
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new IngredientException("Cannot update ingredient in database: " + item.name()));
        }
    }

    @Override
    public Optional<List<Ingredient>> getAll(Connection connection) {
        return Optional.empty();
    }
}
