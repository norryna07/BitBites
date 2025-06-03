package com.bitbites.bitbites2.backend.recipes;

import java.net.URL;

public class Salad extends Recipe {

    /**
     * Create new Recipe object.
     *
     * @param kitchenType  the kitchen type (romanian, italian, etc.)
     * @param instructions the link to the video instructions
     * @param kilocalories the kilocalories on serving
     * @param servings     the number of servings from the recipe
     */
    public Salad(String name, String kitchenType, URL instructions, double kilocalories, int servings) {
        super(name,"Salad", kitchenType, instructions, kilocalories, servings);
    }

    public Salad(int id, String name, String kitchenType, URL instructions, double kilocalories, int servings) {
        super(id, name,"Salad", kitchenType, instructions, kilocalories, servings);
    }
}
