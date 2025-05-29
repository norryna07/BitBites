package main.java.models;

import main.java.mealplans.MealPlan;

public record MealPlanModel(int id, MealPlan mealPlan, int userId) {
}
