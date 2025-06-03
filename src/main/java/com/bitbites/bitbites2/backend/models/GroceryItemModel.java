package com.bitbites.bitbites2.backend.models;

import com.bitbites.bitbites2.backend.groceries.GroceryItem;

public record GroceryItemModel(int id, GroceryItem groceryItem, int listId) {
}
