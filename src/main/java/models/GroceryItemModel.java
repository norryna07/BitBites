package main.java.models;

import main.java.groceries.GroceryItem;

public record GroceryItemModel(int id, GroceryItem groceryItem, int listId) {
}
