package main.java.groceries;

import java.util.HashMap;
import java.util.Map;

/**
 * Store a grocery list with grocery items.
 */
public class GroceryList {
    private Map<Ingredient, GroceryItem> items;

    public GroceryList() {
        items = new HashMap<>();
    }

    /**
     * Add a new item to the grocery list. If already exists an items with the new ingredient,
     * try to merge them (add the quantities if units are compatible).
     * @param newItem the item need to be added
     */
    public void addItem(GroceryItem newItem) {
        Ingredient ingredient = newItem.ingredient();

        // check if already exists the ingredient in map
        if (!items.containsKey(ingredient)) {
            items.put(ingredient, newItem);
        } else {
            GroceryItem existedItem = items.get(ingredient);
            try {
                double quantity = UnitConverter.convert(newItem.quantity(), newItem.unit(), existedItem.unit());
                GroceryItem mergedItem = new GroceryItem(quantity + existedItem.quantity(), existedItem.unit(), existedItem.ingredient());
                items.put(ingredient, mergedItem);
            } catch (IllegalArgumentException e) {
                items.put(ingredient, newItem);
            }
        }
    }

    /**
     * Take 2 GroceryList and add them together in a new GroceryList.
     * @param list1 first list
     * @param list2 second list
     * @return the sum list
     */
    public static GroceryList addLists(GroceryList list1, GroceryList list2) {
        GroceryList sumList = new GroceryList();

        for (GroceryItem item : list1.items.values()) {
            sumList.addItem(item);
        }

        for (GroceryItem item : list2.items.values()) {
            sumList.addItem(item);
        }

        return sumList;
    }

    public String toString(){
        StringBuilder result = new StringBuilder();
        for (GroceryItem item : items.values()) {
            result.append(item.toString() + "\n");
        }
        return result.toString();
    }

    public Map<Ingredient, GroceryItem> getItems() {
        return items;
    }
}
