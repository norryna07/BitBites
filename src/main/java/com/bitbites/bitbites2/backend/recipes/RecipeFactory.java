package com.bitbites.bitbites2.backend.recipes;

import com.bitbites.bitbites2.backend.groceries.GroceryItem;
import com.bitbites.bitbites2.backend.groceries.GroceryList;
import com.bitbites.bitbites2.backend.groceries.Ingredient;
import com.bitbites.bitbites2.backend.groceries.Unit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class RecipeFactory {

    public static Recipe createRecipe(int id, String name, String categoryFood, String kitchenType, URL instructions, double kilocalories, int servings, GroceryList list) {
        Recipe recipe;
        recipe = switch (categoryFood) {
            case "Appetizer" -> new Appetizer(id, name, kitchenType, instructions, kilocalories, servings);
            case "Bread" -> new Bread(id, name, kitchenType, instructions, kilocalories, servings);
            case "Dessert" -> new Dessert(id, name, kitchenType, instructions, kilocalories, servings);
            case "Drink" -> new Drink(id, name, kitchenType, instructions, kilocalories, servings);
            case "MainCourse" -> new MainCourse(id, name, kitchenType, instructions, kilocalories, servings);
            case "Salad" -> new Salad(id, name, kitchenType, instructions, kilocalories, servings);
            case "Soup" -> new Soup(id, name, kitchenType, instructions, kilocalories, servings);
            default -> new MainCourse(id, name, kitchenType, instructions, kilocalories, servings);
        };
        recipe.setGroceryList(list);
        return recipe;
    }

    public static Recipe createRecipe(URL recipeURL) {
        String host = recipeURL.getHost();
        if (host.equals("jamilacuisine.ro") || host.equals("www.jamilacuisine.ro")) {
            return createRecipeFromJamila(recipeURL);
        }
        return null;
    }

    private static Recipe createRecipeFromJamila(URL recipeURL) {

        try {

            Recipe newRecipe = null;

            Document doc = Jsoup.connect(recipeURL.toString()).get();

            // get the name of the recipe
            String recipeName = doc.select("h1.entry-title").text();

            // get the kitchen type
            String recipeKitchenType = doc.select("span.wprm-recipe-cuisine").text();

            // get the number of calories
            String calString = doc.select("span.wprm-recipe-calories").text();
            double recipeCalories;
            try {
                recipeCalories = Double.parseDouble(calString);
            } catch (NumberFormatException e) {
                recipeCalories = 0;
            }

            // get the servings
            String servingString = doc.select("span.wprm-recipe-servings").text();
            int recipeServings;
            try {
                recipeServings = Integer.parseInt(servingString);
            } catch (NumberFormatException e) {
                recipeServings = 1;
            }

            // get the video URL for instructions
            URL recipeVideoURL = getVideoURL(doc);

            // get category food
            String recipeCategoryFood = doc.select("span.wprm-recipe-course").text();

            // create the new recipe using the category food
            switch (recipeCategoryFood) {
                case "Aperitiv" -> newRecipe = new Appetizer(recipeName, recipeKitchenType, recipeVideoURL, recipeCalories, recipeServings);
                case "Paine" -> newRecipe = new Bread(recipeName, recipeKitchenType, recipeVideoURL, recipeCalories, recipeServings);
                case "Desert" -> newRecipe = new Dessert(recipeName, recipeKitchenType, recipeVideoURL, recipeCalories, recipeServings);
                case "Bauturi" -> newRecipe = new Drink(recipeName, recipeKitchenType, recipeVideoURL, recipeCalories, recipeServings);
                case "Fel principal" -> newRecipe = new MainCourse(recipeName, recipeKitchenType, recipeVideoURL, recipeCalories, recipeServings);
                case "Salata" -> newRecipe = new Salad(recipeName, recipeKitchenType, recipeVideoURL, recipeCalories, recipeServings);
                case "Supa / Ciorba" -> newRecipe = new Soup(recipeName, recipeKitchenType, recipeVideoURL, recipeCalories, recipeServings);
                default -> newRecipe = new MainCourse(recipeName, recipeKitchenType, recipeVideoURL, recipeCalories, recipeServings);
            }

            // add the duration
            newRecipe.setDuration(getDuration(doc), "minutes");

            setIngredients(doc, newRecipe);

            return newRecipe;

        } catch (IOException e) {
            System.out.println("Invalid URL" + recipeURL);
            return null;
        }
    }

    private static URL getVideoURL(Document document) {
        Element big_div = document.select("div.epyt-video-wrapper").first();
        Element small_div = big_div.select("> div").first();
        if (small_div != null) {
            String src = small_div.attr("data-facadesrc");

            try {
                URL urlVideo = new URL(src);
                String path = urlVideo.getPath(); // e.g., /embed/VideoID

                // Get the video ID
                String videoId = path.substring(path.lastIndexOf("/") + 1);
                // Build the watchable link
                String watchableLink = "https://www.youtube.com/watch?v=" + videoId;
                return new URL(watchableLink);

            } catch (Exception e) {
                System.err.println("Invalid URL");
                return null;
            }
        }
        return null;
    }

    private static double getDuration(Document doc) {
        String hourString = doc.select("span.wprm-recipe-total_time-hours").text().split(" ")[0];
        String minuteString = doc.select("span.wprm-recipe-total_time-minutes").text().split(" ")[0];
        double totalTime = 0;
        try  {
            totalTime = Double.parseDouble(hourString) * 60;
        } catch (NumberFormatException e) {
            totalTime += 0;
        }
        try {
            totalTime += Double.parseDouble(minuteString);
        } catch (NumberFormatException e) {
            totalTime += 0;
        }
        System.out.println("totalTime: " + totalTime);
        return totalTime;
    }

    private static void setIngredients(Document doc, Recipe recipe) {
        Elements ingredientElements = doc.select("li.wprm-recipe-ingredient");
        for (Element element : ingredientElements) {

            GroceryItem ingredient = null;

            String amountString = element.select("span.wprm-recipe-ingredient-amount").text();
            double amount;
            try {
                amount = Double.parseDouble(amountString);
            } catch (NumberFormatException e) {
                amount = 1;
            }

            String unit = element.select("span.wprm-recipe-ingredient-unit").text();

            String ingredientName = element.select("span.wprm-recipe-ingredient-name").text();

            switch (unit) {
                case "g" -> ingredient = new GroceryItem(amount, Unit.GRAM, new Ingredient(ingredientName, ""));
                case "kg" -> ingredient = new GroceryItem(amount, Unit.KILOGRAM, new Ingredient(ingredientName, ""));
                case "ml" -> ingredient = new GroceryItem(amount, Unit.MILLILITER, new Ingredient(ingredientName, ""));
                case "l" -> ingredient = new GroceryItem(amount, Unit.LITER, new Ingredient(ingredientName, ""));
                case "linguri", "lingura", "lingură" -> ingredient = new GroceryItem(amount, Unit.SPOON, new Ingredient(ingredientName, ""));
                case "cana", "cani", "cană", "căni" -> ingredient = new GroceryItem(amount, Unit.CUP, new Ingredient(ingredientName, ""));
                default -> ingredient = new GroceryItem(amount, Unit.PIECE, new Ingredient(ingredientName, ""));
            }

            recipe.addIngredient(ingredient);
        }
    }
}
