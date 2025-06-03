package com.bitbites.bitbites2.backend.services;

import com.bitbites.bitbites2.backend.groceries.Ingredient;
import com.bitbites.bitbites2.backend.repositories.IngredientsRepository;

public class IngredientsService extends BitBitesService<Ingredient> {
    public IngredientsService() {
        repository = IngredientsRepository.getInstance();
    }
}
