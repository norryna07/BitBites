package main.java.mealplans;

import main.java.recipes.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FamilyMealPlan extends MealPlan {
    private int membersNumber;

    public FamilyMealPlan(int membersNumber) {
        super();
        this.membersNumber = membersNumber;
        dailySchedules = new DailySchedule[7];
    }

    public String getType() {
        return "family";
    }

    public int getMembersNumber() {
        return membersNumber;
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
                .filter(r -> r.getServings() >= membersNumber)
                .toList();
        Recipe recipe;

        // breakfast
        List<Recipe> breakfastOptions = new ArrayList<>(options.stream()
                .filter(r -> r instanceof Appetizer).toList());

        List<Integer> breakfastIndexes = new ArrayList<>();
        while (breakfastIndexes.size() < 7) {
            recipe = breakfastOptions.remove(rand.nextInt(breakfastOptions.size()));

            recipes.put(recipe.getId(),recipe);

            for (int i = 0; i < recipe.getServings() / membersNumber; i++) {
                breakfastIndexes.add(recipe.getId());
            }
        }

        // soup
        List<Recipe> soupOptions = new ArrayList<>(options.stream()
                .filter(r -> r instanceof Soup).toList());
        List<Integer> soupIndexes = new ArrayList<>();
        while (soupIndexes.size() < 7) {
            recipe = soupOptions.remove(rand.nextInt(soupOptions.size()));

            recipes.put(recipe.getId(),recipe);

            for (int i = 0; i < recipe.getServings() / membersNumber; i++) {
                soupIndexes.add(recipe.getId());
            }
        }

        // main course
        List<Recipe> mainCourseOptions = new ArrayList<>(options.stream()
                .filter(r -> r instanceof MainCourse).toList());
        List<Integer> mainCourseIndexes = new ArrayList<>();
        while (mainCourseIndexes.size() < 14) {
            recipe = mainCourseOptions.remove(rand.nextInt(mainCourseOptions.size()));

            recipes.put(recipe.getId(), recipe);

            for (int i = 0; i < recipe.getServings() / membersNumber; i++) {
                mainCourseIndexes.add(recipe.getId());
            }
        }

        // dessert
        List<Recipe> dessertOptions = new ArrayList<>(options.stream()
                .filter(r -> r instanceof Dessert).toList());
        List<Integer> dessertIndexes = new ArrayList<>();
        while (dessertIndexes.size() < 7) {
            recipe = dessertOptions.remove(rand.nextInt(dessertOptions.size()));

            recipes.put(recipe.getId(), recipe);

            for (int i = 0; i < recipe.getServings() / membersNumber; i++) {
                dessertIndexes.add(recipe.getId());
            }
        }

        // generate the daily schedules
        for (int i = 0; i < 7; ++i) {
            // breakfast
            int breakfastIndex = breakfastIndexes.remove(rand.nextInt(breakfastIndexes.size()));

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
