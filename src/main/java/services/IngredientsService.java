package main.java.services;

import main.java.groceries.Ingredient;
import main.java.repositories.IngredientsRepository;

public class IngredientsService extends BitBitesService<Ingredient> {
    public IngredientsService() {
        repository = IngredientsRepository.getInstance();
    }
}
