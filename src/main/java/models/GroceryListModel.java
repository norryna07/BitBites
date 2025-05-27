package main.java.models;

import main.java.groceries.GroceryList;

public record GroceryListModel(int id, GroceryList groceryList, int recipeId, int mealPlanId) {
}
