package com.bitbites.bitbites2.backend.groceries;

public record GroceryItem(double quantity, Unit unit, Ingredient ingredient) {
    public String toString() {
//        return "\uD83D\uDED2 " + quantity + " " + unit + " " + ingredient;
        return quantity + " " + unit + " " + ingredient;
    }
}
