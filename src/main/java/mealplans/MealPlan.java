package main.java.mealplans;

import main.java.groceries.GroceryList;
import main.java.recipes.Recipe;

import java.util.List;

public abstract class MealPlan {
    protected List<Recipe> recipes;
    protected GroceryList groceryList;
    protected DailySchedule[] dailySchedules;

    /**
     * Generate the groceryList from saved recipes
     */
    public void generateGroceryList() {
        groceryList = new GroceryList();
        for (Recipe recipe : recipes) {
            groceryList = GroceryList.addLists(groceryList, recipe.getIngredients());
        }
    }

    public abstract void generatePlan(Object[]... args);

    public void getPlan() {
        for (DailySchedule dailySchedule : dailySchedules) {
            System.out.println(recipes.get(dailySchedule.breakfast()));
            System.out.println(recipes.get(dailySchedule.lunchSoup()));
            System.out.println(recipes.get(dailySchedule.lunchMainCourse()));
            System.out.println(recipes.get(dailySchedule.dinnerMainCourse()));
            System.out.println(recipes.get(dailySchedule.dinnerDessert()));
        }
    }
}
