package main.java.recipes;

import java.net.URL;

public class Soup extends Recipe {

    /**
     * Create new Recipe object.
     *
     * @param kitchenType  the kitchen type (romanian, italian, etc.)
     * @param instructions the link to the video instructions
     * @param kilocalories the kilocalories on serving
     * @param servings     the number of servings from the recipe
     */
    public Soup(String name, String kitchenType, URL instructions, double kilocalories, int servings) {
        super(name,"Soup", kitchenType, instructions, kilocalories, servings);
    }
}
