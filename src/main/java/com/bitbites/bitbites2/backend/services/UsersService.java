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
import com.bitbites.bitbites2.backend.repositories.UsersRepository;
import com.bitbites.bitbites2.backend.users.User;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class UsersService extends BitBitesService<User>{

    public UsersService() {
        repository = UsersRepository.getInstance();
    }

    public User getById(int id) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            User user = repository.getById(connection, id).orElse(null);
            if (user == null) {
                return null;
            }
            List<MealPlanModel> mealPlanModelList = MealPlansRepository.getInstance().getByUserId(connection, user.getId()).orElse(null);
            if (mealPlanModelList == null) {
                return user;
            }
            for (MealPlanModel plan : mealPlanModelList) {
                if (!"self".equals(plan.mealPlan().getType())) {
                    Optional<List<DailyScheduleModel>> dailySchedules = DailySchedulesRepository.getInstance().getByMealPlanId(connection, id);
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
                                RecipesRepository.getInstance().getById(connection, schedule.dailySchedule().breakfast()).orElse(null)
                        );
                        plan.mealPlan().addRecipe(
                                RecipesRepository.getInstance().getById(connection, schedule.dailySchedule().lunchSoup()).orElse(null)
                        );
                        plan.mealPlan().addRecipe(
                                RecipesRepository.getInstance().getById(connection, schedule.dailySchedule().lunchMainCourse()).orElse(null)
                        );
                        plan.mealPlan().addRecipe(
                                RecipesRepository.getInstance().getById(connection, schedule.dailySchedule().dinnerDessert()).orElse(null)
                        );
                        plan.mealPlan().addRecipe(
                                RecipesRepository.getInstance().getById(connection, schedule.dailySchedule().dinnerMainCourse()).orElse(null)
                        );
                    }
                }
            }
            user.setMealPlans(mealPlanModelList.stream()
                    .collect(Collectors.toMap(
                            MealPlanModel::id,
                            MealPlanModel::mealPlan
                    )));
            return user;
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database."));
            return null;
        }
    }
    public User getByName(String name) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            User user = repository.getByName(connection, name).orElse(null);
            if (user == null) {
                return null;
            }
            List<MealPlanModel> mealPlanModelList = MealPlansRepository.getInstance().getByUserId(connection, user.getId()).orElse(null);
            if (mealPlanModelList == null) {
                return user;
            }
            for (MealPlanModel plan : mealPlanModelList) {
                if (!"self".equals(plan.mealPlan().getType())) {
                    Optional<List<DailyScheduleModel>> dailySchedules = DailySchedulesRepository.getInstance().getByMealPlanId(connection, user.getId());
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
                                RecipesRepository.getInstance().getById(connection, schedule.dailySchedule().breakfast()).orElse(null)
                        );
                        plan.mealPlan().addRecipe(
                                RecipesRepository.getInstance().getById(connection, schedule.dailySchedule().lunchSoup()).orElse(null)
                        );
                        plan.mealPlan().addRecipe(
                                RecipesRepository.getInstance().getById(connection, schedule.dailySchedule().lunchMainCourse()).orElse(null)
                        );
                        plan.mealPlan().addRecipe(
                                RecipesRepository.getInstance().getById(connection, schedule.dailySchedule().dinnerDessert()).orElse(null)
                        );
                        plan.mealPlan().addRecipe(
                                RecipesRepository.getInstance().getById(connection, schedule.dailySchedule().dinnerMainCourse()).orElse(null)
                        );
                    }
                } else {
                    List<Recipe> recipes = RecipesRepository.getInstance().getByMealPlanId(connection, plan.id()).orElse(null);
                    for (Recipe recipe : recipes) {
                        System.out.println(recipe);
                    }
                    plan.mealPlan().generatePlan(recipes.toArray(new Recipe[0]));
                }
            }
            user.setMealPlans(mealPlanModelList.stream()
                    .collect(Collectors.toMap(
                            MealPlanModel::id,
                            MealPlanModel::mealPlan
                    )));
            return user;
        } catch (Exception e) {
            AuditService.getInstance().logException(new ConnectionException("Cannot connect to database." + e.getMessage()));
            return null;
        }
    }
}
