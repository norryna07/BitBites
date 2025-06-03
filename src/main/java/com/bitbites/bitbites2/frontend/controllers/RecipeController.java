package com.bitbites.bitbites2.frontend.controllers;

import com.bitbites.bitbites2.backend.recipes.Recipe;
import com.bitbites.bitbites2.backend.services.RecipesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.service.annotation.GetExchange;

@Controller
@RequestMapping("/recipe")
class RecipeController {
    private RecipesService recipesService = new RecipesService();


    @GetMapping({"/", "/index", ""})
    public String index(Model model) {
        model.addAttribute("recipes", recipesService.getAll());
        return "recipe/index";
    }

    @GetMapping("/{id}")
    public String getRecipeDetails(@PathVariable int id, Model model) {
        Recipe recipe = recipesService.getById(id);
        if (recipe == null) {
            return "redirect:/recipe/index";
        }
        model.addAttribute("recipe", recipe);
        model.addAttribute("ingredients", recipe.getIngredients());
        return "recipe/recipe-detail";
    }

}
