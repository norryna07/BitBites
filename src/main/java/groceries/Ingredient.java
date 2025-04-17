package main.java.groceries;

/**
 * Record to store the ingredients used in recipes and grocery lists.
 * @param name the name of the ingredient
 * @param category the category of the ingredient
 */
public record Ingredient(String name, String category) {
    public String toString() {
        return name;
    }
}
