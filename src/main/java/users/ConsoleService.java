package main.java.users;

import main.java.mealplans.*;
import main.java.recipes.Recipe;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleService {
    private static ConsoleService instance;
    private final Map<String, User> users = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);

    private ConsoleService() {}

    public static ConsoleService getInstance() {
        if (instance == null) {
            instance = new ConsoleService();
        }
        return instance;
    }

    public boolean registerUser() {
        System.out.println("Registering new user");

        System.out.println("Please enter your username: ");
        String username = scanner.nextLine();

        if (users.containsKey(username)) {
            System.out.println("Username is already in use");
            return false;
        }

        System.out.println("Please enter your password: ");
        String password = scanner.nextLine();

        String hashed = PasswordUtils.hashPassword(password);
        users.put(username, new User(username, hashed, UserRole.COOKER));

        System.out.println("User " + username + " registered successfully");
        return true;
    }

    public User login() {
        System.out.println("Login");

        System.out.println("Please enter your username: ");
        String username = scanner.nextLine();

        System.out.println("Please enter your password: ");
        String password = scanner.nextLine();

        if (!users.containsKey(username)) {
            System.out.println("User not found");
            return null;
        }
        User user = users.get(username);
        if (user.getHashedPassword().equals(PasswordUtils.hashPassword(password))) {
            System.out.println("Login successful!");
            return user;
        }
        System.out.println("Wrong password");
        return null;
    }

    public void addMealPlan(User user, List<Recipe> recipes) {
        System.out.println("Add meal plan");
        System.out.println("Please enter what type of meal plan do you want to add: ");
        System.out.println("1. Daily Meal Plan");
        System.out.println("2. Weekly Meal Plan");
        System.out.println("3. Family Meal Plan");
        System.out.println("4. Self Meal Plan");

        int type = scanner.nextInt();

        // for self meal plan
        if (type == 4) {
            System.out.println("Please insert the indexes of the recipe you want to add (separated by comma): ");
            String input = scanner.nextLine();

            String[] parts = input.trim().split(",");
            Recipe[] selectedRecipes = new Recipe[parts.length];

            for (int i = 0; i < parts.length; i++) {
                int index = Integer.parseInt(parts[i]);
                selectedRecipes[i] = recipes.get(index);
            }

            SelfMealPlan newPlan = new SelfMealPlan();
            newPlan.generatePlan(selectedRecipes);
            user.addMealPlan(newPlan);
            return;
        }

        int members = 1;
        if (type == 3) {
            System.out.println("Please enter the number of members of your family: ");
            members = scanner.nextInt();
        }

        // get the kitchen types
        System.out.println("Please insert the kitchen types (separated by comma): ");
        String input = scanner.nextLine();
        String[] kitchens = input.trim().split(",");

        // get the maximum duration
        System.out.println("Please insert the maximum duration of the preparation (in hours): ");
        int hours = scanner.nextInt();
        Duration[] duration = new Duration[] {Duration.ofHours(hours)};

        MealPlan newPlan;

        switch (type) {
            case 1 -> newPlan = new DailyMealPlan();
            case 2 -> newPlan = new WeeklyMealPlan();
            case 3 -> newPlan = new FamilyMealPlan(members);
            default -> {
                System.out.println("Invalid meal plan type");
                return;
            }
        }

        newPlan.generatePlan(kitchens, duration, recipes.toArray());
        user.addMealPlan(newPlan);
        System.out.println("Add meal plan successful");
    }

    public void deleteMealPlan(User user) {
        System.out.println("Delete meal plan");
        System.out.println("Please enter the index of the meal plan you want to delete: ");
        int index = scanner.nextInt();
        user.deleteMealPlan(index);
        System.out.println("Meal plan " + index + " deleted successfully");
    }

    public void getMealPlan(User user) {
        System.out.println("Get meal plan");
        System.out.println("Please enter the index of the meal plan you want to get: ");
        int index = scanner.nextInt();
        MealPlan mealPlan = user.getMealPlan(index);
        if (mealPlan == null) {
            return;
        }
        mealPlan.getPlan();
    }
}
