
import emil.Emil;
import main.java.RecipeDatabase;
import main.java.recipes.Recipe;
import main.java.recipes.RecipeFactory;
import main.java.users.ConsoleService;
import main.java.users.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static List<Recipe> recipes;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // initialize the recipe collection
            URL urlRecipes = new URL("https://jamilacuisine.ro/retete-video/");
            recipes = RecipeDatabase.getInstance().generateCategoryRecipes(urlRecipes);
        } catch (Exception e) {
            System.err.println("Invalid URL");
        }

        while (true) {
            System.out.println("Please select what you what to do:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    ConsoleService.getInstance().registerUser();
                    continue;
                }
                case 2 -> {
                    User currentUser = ConsoleService.getInstance().login();
                    UserMenu(currentUser);
                }
                case 3 -> {
                    return;
                }
            }
        }
    }

    public static void UserMenu(User user) {
        while (true) {
            System.out.println("Please select what you want to do: ");
            System.out.println("1. Add meal plan");
            System.out.println("2. Delete meal plan");
            System.out.println("3. Get meal plan");
            System.out.println("4. Exit");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> ConsoleService.getInstance().addMealPlan(user, recipes);
                case 2 -> ConsoleService.getInstance().deleteMealPlan(user);
                case 3 -> {
                    ConsoleService.getInstance().getMealPlan(user);
                }
                case 4 -> {
                    return;
                }
            }
        }
    }
}