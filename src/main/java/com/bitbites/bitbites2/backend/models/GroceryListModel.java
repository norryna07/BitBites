package com.bitbites.bitbites2.backend.models;

import com.bitbites.bitbites2.backend.groceries.GroceryList;

public record GroceryListModel(int id, GroceryList groceryList, int recipeId, int mealPlanId) {
}
