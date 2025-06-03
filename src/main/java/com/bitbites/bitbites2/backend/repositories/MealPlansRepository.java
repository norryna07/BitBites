package com.bitbites.bitbites2.backend.repositories;

import com.bitbites.bitbites2.backend.audit.AuditService;
import com.bitbites.bitbites2.backend.exceptions.MealPlanException;
import com.bitbites.bitbites2.backend.mealplans.FamilyMealPlan;
import com.bitbites.bitbites2.backend.mealplans.MealPlan;
import com.bitbites.bitbites2.backend.mealplans.MealPlanFactory;
import com.bitbites.bitbites2.backend.models.MealPlanModel;
import com.bitbites.bitbites2.backend.recipes.Recipe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MealPlansRepository implements BitBitesRepository<MealPlanModel>{
    private MealPlansRepository() {}
    private static MealPlansRepository instance;
    public static MealPlansRepository getInstance() {
        if(instance == null) {
            instance = new MealPlansRepository();
        }
        return instance;
    }
    @Override
    public int add(Connection connection, MealPlanModel item) {
        String sql = """
                INSERT INTO meal_plans (user_id, type, members)
                VALUES (?, ?::plans_type, ?)
                """;
        int id = 0;
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, item.userId());
            ps.setString(2, item.mealPlan().getType());
            if ("family".equals(item.mealPlan().getType())) {
                FamilyMealPlan familyMealPlan = (FamilyMealPlan) item.mealPlan();
                ps.setLong(3, familyMealPlan.getMembersNumber());
            } else {
                ps.setLong(3, -1);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    AuditService.getInstance().logInsert("meal_plans", rs.getInt("id"));
                    id = rs.getInt("id");
                } else {
                    AuditService.getInstance().logException(new MealPlanException("Cannot add meal plan to database."));
                    return 0;
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new MealPlanException("Cannot add meal plan to database." + e.getMessage()));
            return 0;
        }

        // if the plan is self plan we need to add information in the recipes in self_plan_recipes
        if ("self".equals(item.mealPlan().getType())) {
            // get the recipe_id and complete the table
            String addSql = """
                    INSERT INTO self_plan_recipes (meal_plan_id, recipe_id) 
                    VALUES (?, ?)
                    """;
            List<Recipe> recipes = item.mealPlan().getRecipes();
            for (Recipe recipe : recipes) {
                try (PreparedStatement ps = connection.prepareStatement(addSql)) {
                    ps.setLong(1, id);
                    ps.setLong(2, recipe.getId());
                    int insertedRows = ps.executeUpdate();
                    if (insertedRows == 0) {
                        AuditService.getInstance().logException(new MealPlanException("Cannot add meal plan recipe to database."));
                    } else {
                        AuditService.getInstance().logInsert("self_plan_recipes", recipe.getId());
                    }
                } catch (SQLException e) {
                    AuditService.getInstance().logException(new MealPlanException("Cannot add meal plan recipe to database."));
                }
            }
        }
        return id;
    }

    @Override
    public Optional<MealPlanModel> getById(Connection connection, int id) {
        String sql = """
                SELECT * FROM meal_plans
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                {
                    return Optional.of(new MealPlanModel(
                            id,
                            MealPlanFactory.createMealPlan(rs.getString("type"), rs.getInt("members")),
                            rs.getInt("user_id")
                    ));
                } else {
                    AuditService.getInstance().logException(new MealPlanException("Cannot get meal plan:" + id));
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new MealPlanException("Cannot get meal plan:" + id));
        }
        return Optional.empty();
    }

    @Override
    public Optional<MealPlanModel> getByName(Connection connection, String name) {
        AuditService.getInstance().logException(new MealPlanException("Cannot find meal plan by name."));
        return Optional.empty();
    }

    @Override
    public void deleteById(Connection connection, int id) {
        Optional<MealPlanModel> mealPlanModel = getById(connection, id);
        String sql = """
                DELETE FROM meal_plans
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            int deletedRows = ps.executeUpdate();
            if (deletedRows == 0) {
                AuditService.getInstance().logException(new MealPlanException("Cannot delete meal plan: " + id));
                return;
            } else {
                AuditService.getInstance().logDelete("meal_plans", id);
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new MealPlanException("Cannot delete meal plan: " + id));
            return;
        }
    }

    @Override
    public void deleteByName(Connection connection, String name) {
        AuditService.getInstance().logException(new MealPlanException("Cannot find meal plan by name."));
    }

    @Override
    public void update(Connection connection, MealPlanModel item) {
        AuditService.getInstance().logException(new MealPlanException("Cannot update meal plan to database."));
    }

    @Override
    public Optional<List<MealPlanModel>> getAll(Connection connection) {
        String sql = """
                SELECT id, user_id, type
                FROM meal_plans
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            List<MealPlanModel> mealPlans = new ArrayList<>();
            while (rs.next()) {
                mealPlans.add(new MealPlanModel(
                        rs.getInt("id"),
                        MealPlanFactory.createMealPlan(rs.getString("type"), -1),
                        rs.getInt("user_id")
                ));
            }
            return Optional.of(mealPlans);
        } catch (SQLException e) {
            AuditService.getInstance().logException(new MealPlanException("Cannot get meal plans"));
        }
        return Optional.empty();
    }

    public Optional<List<MealPlanModel>> getByUserId(Connection connection, int userId) {
        String sql = """
                SELECT id, user_id, type
                FROM meal_plans
                where user_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try(ResultSet rs = ps.executeQuery()) {
                List<MealPlanModel> mealPlans = new ArrayList<>();
                while (rs.next()) {
                    mealPlans.add(new MealPlanModel(
                            rs.getInt("id"),
                            MealPlanFactory.createMealPlan(rs.getString("type"), -1),
                            rs.getInt("user_id")
                    ));
                }
                return Optional.of(mealPlans);
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new MealPlanException("Cannot get meal plans"));
        }
        return Optional.empty();
    }
}
