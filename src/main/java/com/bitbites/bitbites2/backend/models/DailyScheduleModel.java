package com.bitbites.bitbites2.backend.models;

import com.bitbites.bitbites2.backend.mealplans.DailySchedule;

public record DailyScheduleModel(int id, DailySchedule dailySchedule, int mealPlanId) {
}
