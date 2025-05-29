package main.java.mealplans;

public class MealPlanFactory {

    public static MealPlan createMealPlan(String type, int members) {
        return switch (type) {
            case "daily" -> new DailyMealPlan();
            case "family" -> new FamilyMealPlan(members);
            case "self" -> new SelfMealPlan();
            case "weekly" -> new WeeklyMealPlan();
            default -> null;
        };
    }
}
