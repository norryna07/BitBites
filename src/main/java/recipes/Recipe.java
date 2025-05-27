package main.java.recipes;

import main.java.groceries.GroceryItem;
import main.java.groceries.GroceryList;

import java.net.URL;
import java.time.Duration;

public abstract class Recipe {
    protected int id;
    protected String name;
    protected GroceryList ingredients;
    protected String categoryFood;
    protected String kitchenType;
    protected double kilocalories;
    protected int servings;
    protected URL instructions;
    protected Duration duration;

    /**
     * Create new Recipe object.
     *
     * @param name  name of the recipe
     * @param categoryFood the food category
     * @param kitchenType  the kitchen type (romanian, italian, etc.)
     * @param instructions the link to the video instructions
     * @param kilocalories the kilocalories on serving
     * @param servings the number of servings from the recipe
     */
    public Recipe(String name, String categoryFood, String kitchenType, URL instructions, double kilocalories, int servings) {
        this.name = name;
        this.categoryFood = categoryFood;
        this.kitchenType = kitchenType;
        this.instructions = instructions;
        this.kilocalories = kilocalories;
        this.servings = servings;
        ingredients = new GroceryList();
    }

    public Recipe(int id, String name, String categoryFood, String kitchenType, URL instructions, double kilocalories, int servings) {
        this.id = id;
        this.name = name;
        this.categoryFood = categoryFood;
        this.kitchenType = kitchenType;
        this.instructions = instructions;
        this.kilocalories = kilocalories;
        this.servings = servings;
        ingredients = new GroceryList();
    }

    /**
     * Add a new ingredient in the list of ingredients used.
     * @param item the ingredient to add.
     */
    public void addIngredient(GroceryItem item) {
        ingredients.addItem(item);
    }

    /**
     * Set duration from a number and a time unit.
     * @param quantity the time in numbers
     * @param unit the unit for time (hours, minutes, seconds)
     */
    public void setDuration(double quantity, String unit) {
        unit = unit.toLowerCase();
        switch (unit) {
            case "hours", "hour", "h" -> {
                duration = Duration.ofHours((long)quantity);
            }
            case "minutes", "minute", "m" -> {
                duration = Duration.ofMinutes((long)quantity);
            }
            case "seconds", "second", "s" -> {
                duration = Duration.ofSeconds((long)quantity);
            }
            default -> {
                duration = null;
            }
        }
    }

    public GroceryList getIngredients() {
        return ingredients;
    }

    public String getCategoryFood() {
        return categoryFood;
    }

    public String getKitchenType() {
        return kitchenType;
    }

    public double getKilocalories() {
        return kilocalories;
    }

    public int getServings() {
        return servings;
    }

    public URL getInstructions() {
        return instructions;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setGroceryList(GroceryList groceryList) {
        this.ingredients = groceryList;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: " + name + "\n");
        sb.append("Category: " + categoryFood + "\n");
        sb.append("Kitchen: " + kitchenType + "\n");
        sb.append("Servings: " + servings + "\n");
        sb.append("Duration: " + duration + "\n");
        sb.append("Kilocalories: " + kilocalories + "\n");
        sb.append("Ingredients: " + ingredients + "\n");
        sb.append("Intructions: " + instructions + "\n");
        return sb.toString();
    }

}
