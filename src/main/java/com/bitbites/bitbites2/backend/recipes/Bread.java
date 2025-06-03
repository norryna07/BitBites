package com.bitbites.bitbites2.backend.recipes;

import java.net.URL;

public class Bread extends Recipe {

    /**
     * Create new Recipe object.
     *
     * @param kitchenType  the kitchen type (romanian, italian, etc.)
     * @param instructions the link to the video instructions
     * @param kilocalories the kilocalories on serving
     * @param servings     the number of servings from the recipe
     */
    public Bread(String name, String kitchenType, URL instructions, double kilocalories, int servings) {
        super(name,"Bread", kitchenType, instructions, kilocalories, servings);
    }

    public Bread(int id, String name, String kitchenType, URL instructions, double kilocalories, int servings) {
        super(id, name,"Bread", kitchenType, instructions, kilocalories, servings);
    }
}
