package com.bitbites.bitbites2.backend.groceries;

/**
 * Record to store the ingredients used in recipes and grocery lists.
 * @param name the name of the ingredient
 * @param category the category of the ingredient
 */
public record Ingredient(String name, String category) {
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Ingredient) {
            return name.equals(((Ingredient) o).name);
        }
        return false;
    }
}
