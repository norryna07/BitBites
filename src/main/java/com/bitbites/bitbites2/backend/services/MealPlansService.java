package com.bitbites.bitbites2.backend.services;

import com.bitbites.bitbites2.backend.audit.AuditService;
import com.bitbites.bitbites2.backend.config.ConnectionProvider;
import com.bitbites.bitbites2.backend.exceptions.ConnectionException;
import com.bitbites.bitbites2.backend.mealplans.DailySchedule;
import com.bitbites.bitbites2.backend.models.DailyScheduleModel;
import com.bitbites.bitbites2.backend.models.MealPlanModel;
import com.bitbites.bitbites2.backend.recipes.Recipe;
import com.bitbites.bitbites2.backend.repositories.DailySchedulesRepository;
import com.bitbites.bitbites2.backend.repositories.MealPlansRepository;
import com.bitbites.bitbites2.backend.repositories.RecipesRepository;

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

    public int add(MealPlanModel item) {
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
            return id;
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
        }
        return 0;
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
            } else {
                plan.mealPlan().generatePlan(
                        recipesRepository.getByMealPlanId(connection, plan.id()).get().toArray(new Recipe[0])
                );
            }
            return plan;
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
            return null;
        }
    }
}
