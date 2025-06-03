package com.bitbites.bitbites2.backend;

import com.bitbites.bitbites2.backend.recipes.Recipe;
import com.bitbites.bitbites2.backend.recipes.RecipeFactory;
import com.bitbites.bitbites2.backend.services.RecipesService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RecipeDatabase {

    private static List<String> categories;
    final private static String allRecipes = "https://jamilacuisine.ro/retete-video/";
    private static RecipesService recipesService = new RecipesService();;
    private static RecipeDatabase instance = null;

    private RecipeDatabase(){
        categories = new ArrayList<>();
        categories.add("https://jamilacuisine.ro/retete-video/aperitive/");
        categories.add("https://jamilacuisine.ro/retete-video/ciorbe-si-supe/");
        categories.add("https://jamilacuisine.ro/retete-video/dulciuri/");
        categories.add("https://jamilacuisine.ro/retete-video/mancaruri/");
        categories.add("https://jamilacuisine.ro/retete-video/paine/");
        categories.add("https://jamilacuisine.ro/retete-video/salate/");
        categories.add("https://jamilacuisine.ro/retete-video/torturi/");

    }

    public String getCategory(String category){
        return switch (category) {
            case "Appetizer" -> categories.get(0);
            case "Soup" -> categories.get(1);
            case "Sweets" -> categories.get(2);
            case "Main Course" -> categories.get(3);
            case "Bread" -> categories.get(4);
            case "Salad" -> categories.get(5);
            case "Cake" -> categories.get(6);
            default -> categories.get(0);
        };
    }

    public static RecipeDatabase getInstance(){
        if (instance == null){
            instance = new RecipeDatabase();
        }
        return instance;
    }

    public List<Recipe> generateCategoryRecipes(URL categoryPage)
    {
        List<Recipe> recipes = new LinkedList<>();

        // get the recipes from the first page of the category
        List<Recipe> page = generateRecipeList(categoryPage);
        if (page != null && !page.isEmpty()){
            recipes.addAll(page);
        }

        // take the other pages of this category and extract the recipes
        try {
            Document doc = Jsoup.connect(categoryPage.toString()).get();

            Elements pagesElements = doc.select("a.page-numbers");

            for (Element pageElement : pagesElements){
                String pageURL = pageElement.attr("href");
                System.out.println(pageURL);
                page = generateRecipeList(new URL(pageURL));
                if (page != null && !page.isEmpty()){
                    recipes.addAll(page);
                }
            }

            return recipes;

        } catch (IOException e) {
            System.out.println("Invalid URL" + categoryPage.toString());
            return null;
        }
    }

    private List<Recipe> generateRecipeList(URL recipeMenuPage)
    {
        List<Recipe> recipes = new LinkedList<Recipe>();
        try {
            Document doc = Jsoup.connect(recipeMenuPage.toString()).get();

            // get the elements from the menu page
            Elements elements = doc.select("h2.entry-title");
            for (Element element : elements) {
                Element aElement = element.select("a").first();
                String URLString = aElement.attr("href");
                URL recipePage = new URL(URLString);
                System.out.println(URLString);
                Recipe recipe = RecipeFactory.createRecipe(recipePage);
                recipesService.add(recipe);
            }
            return recipes;

        } catch (IOException e) {
            System.err.println("Invalid URL" + recipeMenuPage.toString());
            return null;
        }
    }

}
