package com.bitbites.bitbites2.backend.repositories;

import com.bitbites.bitbites2.backend.audit.AuditService;
import com.bitbites.bitbites2.backend.exceptions.DailyScheduleException;
import com.bitbites.bitbites2.backend.mealplans.DailySchedule;
import com.bitbites.bitbites2.backend.models.DailyScheduleModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DailySchedulesRepository implements BitBitesRepository<DailyScheduleModel>{
    private static DailySchedulesRepository instance = null;
    private DailySchedulesRepository() {}
    public static DailySchedulesRepository getInstance() {
        if (instance == null) {
            instance = new DailySchedulesRepository();
        }
        return instance;
    }

    @Override
    public int add(Connection connection, DailyScheduleModel item) {
        String sql = """
                INSERT INTO daily_schedules (meal_plan_id, breakfast_index, lunch_soup_index, lunch_main_course_index, dinner_main_course_index, dinner_dessert_index)
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, item.mealPlanId());
            ps.setLong(2, item.dailySchedule().breakfast());
            ps.setLong(3, item.dailySchedule().lunchSoup());
            ps.setLong(4, item.dailySchedule().lunchMainCourse());
            ps.setLong(5, item.dailySchedule().dinnerMainCourse());
            ps.setLong(6, item.dailySchedule().dinnerDessert());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    AuditService.getInstance().logInsert("daily_schedules", rs.getInt("id"));
                    return rs.getInt("id");
                } else {
                    AuditService.getInstance().logException(new DailyScheduleException("Cannot add daily schedule to database."));
                }
            }

        } catch (SQLException e) {
            AuditService.getInstance().logException(new DailyScheduleException("Cannot add daily schedule to database."));
        }
        return 0;
    }

    @Override
    public Optional<DailyScheduleModel> getById(Connection connection, int id) {
        String sql = """
                SELECT meal_plan_id, breakfast_index, lunch_soup_index, lunch_main_course_index, dinner_main_course_index, dinner_dessert_index
                FROM daily_schedules
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new DailyScheduleModel(
                            id,
                            new DailySchedule(
                                    rs.getInt("breakfast_index"),
                                    rs.getInt("lunch_soup_index"),
                                    rs.getInt("lunch_main_course_index"),
                                    rs.getInt("dinner_main_course_index"),
                                    rs.getInt("dinner_dessert_index")
                            ),
                            rs.getInt("meal_plan_id")
                    ));
                } else  {
                    AuditService.getInstance().logException(new DailyScheduleException("Cannot get daily schedule from database."));
                }
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new DailyScheduleException("Cannot get daily schedule from database."));
        }
        return Optional.empty();
    }

    @Override
    public Optional<DailyScheduleModel> getByName(Connection connection, String name) {
        AuditService.getInstance().logException(new DailyScheduleException("Cannot get daily schedule from database by name."));
        return Optional.empty();
    }

    @Override
    public void deleteById(Connection connection, int id) {
        String sql = """
                DELETE FROM daily_schedules
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            int deletedRows = ps.executeUpdate();
            if (deletedRows == 0) {
                AuditService.getInstance().logException(new DailyScheduleException("Cannot delete daily schedule from database."));
            } else {
                AuditService.getInstance().logDelete("daily_schedules", id);
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new DailyScheduleException("Cannot delete daily schedule from database."));
        }
    }

    @Override
    public void deleteByName(Connection connection, String name) {
        AuditService.getInstance().logException(new DailyScheduleException("Cannot delete daily schedule from database by name."));
    }

    @Override
    public void update(Connection connection, DailyScheduleModel item) {
        String sql = """
                UPDATE daily_schedules
                SET meal_plan_id = ?, breakfast_index = ?, lunch_soup_index = ?, lunch_main_course_index = ?, dinner_main_course_index = ?, dinner_dessert_index = ?
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, item.mealPlanId());
            ps.setLong(2, item.dailySchedule().breakfast());
            ps.setLong(3, item.dailySchedule().lunchSoup());
            ps.setLong(4, item.dailySchedule().lunchMainCourse());
            ps.setLong(5, item.dailySchedule().dinnerMainCourse());
            ps.setLong(6, item.dailySchedule().dinnerDessert());
            ps.setLong(7, item.id());

            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                AuditService.getInstance().logException(new DailyScheduleException("Cannot update daily schedule in database."));
            } else {
                AuditService.getInstance().logUpdate("daily_schedules", item.id());
            }
        } catch (SQLException e) {
            AuditService.getInstance().logException(new DailyScheduleException("Cannot update daily schedule in database."));
        }
    }

    @Override
    public Optional<List<DailyScheduleModel>> getAll(Connection connection) {
        String sql = """
                SELECT id, meal_plan_id, breakfast_index, lunch_soup_index, lunch_main_course_index, dinner_main_course_index, dinner_dessert_index
                FROM daily_schedules
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            List<DailyScheduleModel> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new DailyScheduleModel(
                        rs.getInt("id"),
                        new DailySchedule(
                                rs.getInt("breakfast_index"),
                                rs.getInt("lunch_soup_index"),
                                rs.getInt("lunch_main_course_index"),
                                rs.getInt("dinner_main_course_index"),
                                rs.getInt("dinner_dessert_index")
                        ),
                        rs.getInt("meal_plan_id")
                ));
            }
            return Optional.of(list);
        } catch (SQLException e) {
            AuditService.getInstance().logException(new DailyScheduleException("Cannot get all daily schedules from database."));
        }
        return Optional.empty();
    }

    public Optional<List<DailyScheduleModel>> getByMealPlanId(Connection connection, int mealPlanId) {
        String sql = """
                SELECT id, meal_plan_id, breakfast_index, lunch_soup_index, lunch_main_course_index, dinner_main_course_index, dinner_dessert_index
                FROM daily_schedules
                WHERE meal_plan_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, mealPlanId);
            try (ResultSet rs = ps.executeQuery()) {
                List<DailyScheduleModel> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new DailyScheduleModel(
                            rs.getInt("id"),
                            new DailySchedule(
                                    rs.getInt("breakfast_index"),
                                    rs.getInt("lunch_soup_index"),
                                    rs.getInt("lunch_main_course_index"),
                                    rs.getInt("dinner_main_course_index"),
                                    rs.getInt("dinner_dessert_index")
                            ),
                            rs.getInt("meal_plan_id")
                    ));
            }
            return Optional.of(list);
        }
        }catch (SQLException e) {
            AuditService.getInstance().logException(new DailyScheduleException("Cannot get all daily schedules from database."));
        }
        return Optional.empty();
    }
}
