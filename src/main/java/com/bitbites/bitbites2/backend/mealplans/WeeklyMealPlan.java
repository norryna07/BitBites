package com.bitbites.bitbites2.backend.mealplans;

import com.bitbites.bitbites2.backend.recipes.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WeeklyMealPlan extends MealPlan {
    public WeeklyMealPlan() {
        super();
        dailySchedules = new DailySchedule[7];
    }

    public String getType() {
        return "weekly";
    }

    @Override
    public void generatePlan(Object[]... preferences) {


        if (preferences.length != 2 && preferences.length != 3) {
            System.err.println("Invalid number of arguments");
            return;
        }

        if (!(preferences[0] instanceof String[])) {
            System.err.println("Invalid kitchen types");
            return;
        }

        if (!(preferences[1] instanceof Duration[]) || preferences[1].length != 1) {
            System.err.println("Invalid duration types");
            return;
        }

        // optional the last argument will be the array with the recipes i need to choice from
        if (!(preferences[2] instanceof Recipe[])) {
            System.err.println("Invalid recipe database");
            return;
        }

        String[] kitchens = (String[]) preferences[0];
        Duration maxDuration = (Duration)preferences[1][0];
        Recipe[] recipesDatabase = (Recipe[]) preferences[2];

        Random rand = new Random();

        List<Recipe> options = Arrays.stream(recipesDatabase)
                .filter(r -> r.getDuration().minus(maxDuration).isNegative())
                .filter(r -> Arrays.asList(kitchens).contains(r.getKitchenType()))
                .toList();

        // select the breakfast options
        List<Recipe> breakfastOptions = new ArrayList<>(options.stream()
                .filter(r -> r instanceof Appetizer).toList());

        // for breakfast: select recipes until I obtain 7 servings
        List<Integer> appetizerIndexes = new ArrayList<>();
        Recipe recipe;
        while (appetizerIndexes.size() < 7) {
            recipe = breakfastOptions.remove(rand.nextInt(breakfastOptions.size()));

            recipes.put(recipe.getId(), recipe);

            for (int i = 0; i < recipe.getServings(); ++i) {
                appetizerIndexes.add(recipe.getId());
            }
        }

        // select the soup options
        List<Recipe> soupOptions = new ArrayList<>(options.stream()
                .filter(r -> r instanceof Soup).toList());

        // for soup: select recipes until I obtain 7 servings
        List<Integer> soupIndexes = new ArrayList<>();
        while (soupIndexes.size() < 7) {
            recipe = soupOptions.remove(rand.nextInt(soupOptions.size()));

            recipes.put(recipe.getId(), recipe);

            for (int i = 0; i < recipe.getServings(); ++i) {
                soupIndexes.add(recipe.getId());
            }
        }

        // select the main course options
        List<Recipe> mainCourseOptions = new ArrayList<>(options.stream()
                .filter(r -> r instanceof MainCourse).toList());

        // for main course: select recipes until I obtain 14 servings (lunch and dinner)
        List<Integer> mainCourseIndexes = new ArrayList<>();
        while (mainCourseIndexes.size() < 14) {
            recipe = mainCourseOptions.remove(rand.nextInt(mainCourseOptions.size()));

            recipes.put(recipe.getId(), recipe);

            for (int i = 0; i < recipe.getServings(); ++i) {
                mainCourseIndexes.add(recipe.getId());
            }
        }

        // select the dessert options
        List<Recipe> dessertOptions = new ArrayList<>(options.stream()
                .filter(r -> r instanceof Dessert).toList());

        // for dessert: select recipes until I obtain 7 servings
        List<Integer> dessertIndexes = new ArrayList<>();
        while (dessertIndexes.size() < 7) {
            recipe = dessertOptions.remove(rand.nextInt(dessertOptions.size()));

            recipes.put(recipe.getId(), recipe);

            for (int i = 0; i < recipe.getServings(); ++i) {
                dessertIndexes.add(recipe.getId());
            }
        }

        // generate the daily schedules
        for (int i = 0; i < 7; ++i) {
            // breakfast
            int breakfastIndex = appetizerIndexes.remove(rand.nextInt(appetizerIndexes.size()));

            // lunch soup
            int lunchSoupIndex = soupIndexes.remove(rand.nextInt(soupIndexes.size()));

            // lunch main course
            int lunchMainCourseIndex = mainCourseIndexes.remove(rand.nextInt(mainCourseIndexes.size()));

            // dinner main course
            int dinnerMainCourseIndex = mainCourseIndexes.remove(rand.nextInt(mainCourseIndexes.size()));

            // dinner dessert
            int dinnerDessertIndex = dessertIndexes.remove(rand.nextInt(dessertIndexes.size()));

            dailySchedules[i] = new DailySchedule(breakfastIndex, lunchSoupIndex, lunchMainCourseIndex, dinnerMainCourseIndex, dinnerDessertIndex);
        }
    }
}
