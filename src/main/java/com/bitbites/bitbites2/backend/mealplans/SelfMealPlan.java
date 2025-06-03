package com.bitbites.bitbites2.backend.mealplans;

import com.bitbites.bitbites2.backend.audit.AuditService;
import com.bitbites.bitbites2.backend.recipes.Recipe;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SelfMealPlan extends MealPlan {

    /**
     * Generate the recipe list for SelfMealPlan.
     * @param args an array of Recipe because here the recipes are picked by the user.
     */
    @Override
    public void generatePlan(Object[]... args) {
        AuditService.getInstance().log("bau");
        Recipe[] localRecipes = (Recipe[]) args[0];
        AuditService.getInstance().log(localRecipes.toString());
        if (!(args[0] instanceof Recipe[])) {
            return;
        }
//        Recipe[] localRecipes = (Recipe[]) args[0];
        recipes = Arrays.stream(localRecipes)
                .collect(Collectors.toMap(Recipe::getId, Function.identity()));
        AuditService.getInstance().log(recipes.toString());
    }

    public String getType() {
        return "self";
    }

    @Override
    public void getPlan() {
        for (Recipe recipe : recipes.values()) {
            System.out.println(recipe);
        }
    }
}
