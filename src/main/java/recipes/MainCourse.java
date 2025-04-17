package main.java.recipes;

import java.net.URL;

public class MainCourse extends Recipe {
    /**
     * Create new Recipe object.
     *
     * @param kitchenType  the kitchen type (romanian, italian, etc.)
     * @param instructions the link to the video instructions
     * @param kilocalories the kilocalories on serving
     * @param servings     the number of servings from the recipe
     */
    public MainCourse(String name, String kitchenType, URL instructions, double kilocalories, int servings) {
        super(name,"MainCourse", kitchenType, instructions, kilocalories, servings);
    }
}
