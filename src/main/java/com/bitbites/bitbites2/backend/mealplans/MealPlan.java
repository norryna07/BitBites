package com.bitbites.bitbites2.backend.mealplans;

import com.bitbites.bitbites2.backend.groceries.GroceryList;
import com.bitbites.bitbites2.backend.recipes.Recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MealPlan {
    protected Map<Integer, Recipe> recipes;
    protected GroceryList groceryList;
    protected DailySchedule[] dailySchedules;

    protected MealPlan() {
        recipes = new HashMap<Integer, Recipe>();
    }

    /**
     * Generate the groceryList from saved recipes
     */
    public void generateGroceryList() {
        groceryList = new GroceryList();
        for (Recipe recipe : recipes.values()) {
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

    public List<Recipe> getRecipes() {
        return recipes.values().stream().toList();
    }

    public Map<Integer, Recipe> getMapRecipes() {
        return recipes;
    }

    public void addRecipe(Recipe recipe) {
        recipes.put(recipe.getId(), recipe);
    }

    public DailySchedule[] getDailySchedules() {
        return dailySchedules;
    }

    public void setDailySchedules(DailySchedule[] dailySchedules) {
        this.dailySchedules = dailySchedules;
    }

    public GroceryList getGroceryList() {
        groceryList.sortedGroceryList();
        return groceryList;
    }

    public void setGroceryList(GroceryList groceryList) {
        this.groceryList = groceryList;
    }

    public abstract String getType();
}
