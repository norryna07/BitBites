package com.bitbites.bitbites2.backend.models;

import com.bitbites.bitbites2.backend.mealplans.MealPlan;

public record MealPlanModel(int id, MealPlan mealPlan, int userId) {
}
