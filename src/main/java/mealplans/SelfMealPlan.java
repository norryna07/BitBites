package main.java.mealplans;

import main.java.recipes.Recipe;

import java.util.Arrays;

public class SelfMealPlan extends MealPlan {

    /**
     * Generate the recipe list for SelfMealPlan.
     * @param args an array of Recipe because here the recipes are picked by the user.
     */
    @Override
    public void generatePlan(Object[]... args) {
        if (!(args[0] instanceof Recipe[])) {
            return;
        }
        Recipe[] localRecipes = (Recipe[]) args[0];
        recipes = Arrays.asList(localRecipes);
    }

    @Override
    public void getPlan() {
        for (Recipe recipe : recipes) {
            System.out.println(recipe);
        }
    }
}
