package main.java.services;

import main.java.audit.AuditService;
import main.java.config.ConnectionProvider;
import main.java.exceptions.ConnectionException;
import main.java.mealplans.DailySchedule;
import main.java.models.DailyScheduleModel;
import main.java.models.MealPlanModel;
import main.java.repositories.DailySchedulesRepository;
import main.java.repositories.MealPlansRepository;
import main.java.repositories.RecipesRepository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class MealPlansService extends BitBitesService<MealPlanModel> {
    DailySchedulesRepository dailySchedulesRepository;
    RecipesRepository recipesRepository;
    public MealPlansService() {
        repository = MealPlansRepository.getInstance();
        dailySchedulesRepository = DailySchedulesRepository.getInstance();
        recipesRepository = RecipesRepository.getInstance();
    }

    public void add(MealPlanModel item) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            int id = repository.add(connection, item);
            if (!"self".equals(item.mealPlan().getType())) {
                // we need to add the daily plans
                for (var schedule : item.mealPlan().getDailySchedules()) {
                    dailySchedulesRepository.add(connection,
                            new DailyScheduleModel(
                                    0,
                                    schedule,
                                    id
                            ));
                }
            }
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
    }

    public MealPlanModel getById(int id) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            MealPlanModel plan = repository.getById(connection, id).orElse(null);
            if (plan == null) {
                return null;
            }
            if (!"self".equals(plan.mealPlan().getType())) {
                Optional<List<DailyScheduleModel>> dailySchedules = dailySchedulesRepository.getByMealPlanId(connection, id);
                if (!dailySchedules.isPresent()) {
                    return null;
                }
                plan.mealPlan().setDailySchedules(
                        dailySchedules.get().
                                stream().
                                map(DailyScheduleModel::dailySchedule)
                                .toArray(DailySchedule[]::new)
                );
                // we need to add the recipes
                for (var schedule : dailySchedules.get()) {
                    plan.mealPlan().addRecipe(
                            recipesRepository.getById(connection, schedule.dailySchedule().breakfast()).orElse(null)
                    );
                    plan.mealPlan().addRecipe(
                            recipesRepository.getById(connection, schedule.dailySchedule().lunchSoup()).orElse(null)
                    );
                    plan.mealPlan().addRecipe(
                            recipesRepository.getById(connection, schedule.dailySchedule().lunchMainCourse()).orElse(null)
                    );
                    plan.mealPlan().addRecipe(
                            recipesRepository.getById(connection, schedule.dailySchedule().dinnerDessert()).orElse(null)
                    );
                    plan.mealPlan().addRecipe(
                            recipesRepository.getById(connection, schedule.dailySchedule().dinnerMainCourse()).orElse(null)
                    );
                }
            }
            return plan;
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
            return null;
        }
    }
}
