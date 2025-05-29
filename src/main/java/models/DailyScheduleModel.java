package main.java.models;

import main.java.mealplans.DailySchedule;

public record DailyScheduleModel(int id, DailySchedule dailySchedule, int mealPlanId) {
}
