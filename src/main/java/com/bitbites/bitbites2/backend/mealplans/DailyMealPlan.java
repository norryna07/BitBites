package com.bitbites.bitbites2.backend.mealplans;

import com.bitbites.bitbites2.backend.recipes.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DailyMealPlan extends MealPlan {
    public DailyMealPlan() {
        super();
        dailySchedules = new DailySchedule[1];
    }

    public String getType() {
        return "daily";
    }

    /**
     * Generate the daily plan based on preferences
     * @param preferences contains:
     *                    - an array of kitchen for food,
     *                    - an array with 1 element: the maximum duration (Duration element)
     */
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
        List<Recipe> breakfastOptions = options.stream()
                .filter(r -> r instanceof Appetizer).toList();

        // select with random the breakfast
        Recipe breakfast = breakfastOptions.get(rand.nextInt(breakfastOptions.size()));
        recipes.put(breakfast.getId(), breakfast);

        // select the lunchSoup options
        List<Recipe> lunchSoupOptions = options.stream()
                .filter(r -> r instanceof Soup).toList();

        // select with random the lunch soup
        Recipe lunchSoup = lunchSoupOptions.get(rand.nextInt(lunchSoupOptions.size()));
        recipes.put(lunchSoup.getId(),lunchSoup);

        // select the lunchMainCourse options
        List<Recipe> lunchMainCourseOptions = options.stream()
                .filter(r -> r instanceof MainCourse).toList();

        // select with random the lunch main course
        Recipe lunchMainCourse = lunchMainCourseOptions.get(rand.nextInt(lunchMainCourseOptions.size()));
        recipes.put(lunchMainCourse.getId(),lunchMainCourse);

        // select the dinnerMainCourse options
        List<Recipe> dinnerMainCourseOptions = options.stream()
                .filter(r -> r instanceof MainCourse).toList();

        // select with random the dinner main course
        Recipe dinnerMainCourse = dinnerMainCourseOptions.get(rand.nextInt(dinnerMainCourseOptions.size()));
        recipes.put(dinnerMainCourse.getId(), dinnerMainCourse);

        // select the dinnerDessert options
        List<Recipe> dinnerDessertOptions = options.stream()
                .filter(r -> r instanceof Dessert).toList();

        // select with random the dinner dessert
        Recipe dinnerDessert = dinnerDessertOptions.get(rand.nextInt(dinnerDessertOptions.size()));
        recipes.put(dinnerDessert.getId(), dinnerDessert);


        dailySchedules[0] = new DailySchedule(
                breakfast.getId(),
                lunchSoup.getId(),
                lunchMainCourse.getId(),
                dinnerMainCourse.getId(),
                dinnerDessert.getId());
    }
}
