package com.bitbites.bitbites2.frontend.controllers;

import com.bitbites.bitbites2.backend.mealplans.DailyMealPlan;
import com.bitbites.bitbites2.backend.mealplans.FamilyMealPlan;
import com.bitbites.bitbites2.backend.mealplans.SelfMealPlan;
import com.bitbites.bitbites2.backend.mealplans.WeeklyMealPlan;
import com.bitbites.bitbites2.backend.models.MealPlanModel;
import com.bitbites.bitbites2.backend.recipes.Recipe;
import com.bitbites.bitbites2.backend.services.MealPlansService;
import com.bitbites.bitbites2.backend.services.RecipesService;
import com.bitbites.bitbites2.backend.users.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/plans")
class MealPlansController {
    private RecipesService recipesService;
    private MealPlansService mealPlansService;

    public MealPlansController() {
        this.recipesService = new RecipesService();
        this.mealPlansService = new MealPlansService();
    }

    @GetMapping({"/myPlans", "/", ""})
    public String myPlans(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("mealPlans", user.getMealPlans());
        return "plans/myPlans";
    }

    @GetMapping("/create")
    public String create(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");
        return "plans/create";
    }

    @PostMapping("/create")
    public String createMealPlanRedirect(@RequestParam String type) {
        return switch (type.toLowerCase()) {
            case "daily" -> "redirect:/plans/create/daily";
            case "weekly" -> "redirect:/plans/create/weekly";
            case "family" -> "redirect:/plans/create/family";
            case "self" -> "redirect:/plans/create/self";
            default -> "redirect:/plans/create";
        };
    }

    @GetMapping("/create/self")
    public String createSelf(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("recipes", recipesService.getAll());
        return "plans/create/self";
    }

    @PostMapping("/create/self")
    public String createSelfMealPlan(HttpSession session, @RequestParam("selectedRecipes") List<Long> selectedRecipes, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");

        SelfMealPlan plan = new SelfMealPlan();

        // You'd need to fetch the recipes by ID from the database
        List<Recipe> recipes = selectedRecipes.stream()
                        .map(id -> recipesService.getById(id.intValue()))
                        .toList();

        plan.generatePlan(recipes.toArray(new Recipe[0]));

        int mealId = mealPlansService.add(new MealPlanModel(
                0,
                plan,
                user.getId()
        ));
        user.addMealPlan(plan,mealId);

        return "redirect:/plans/myPlans"; // return the view showing the generated plan
    }

    @GetMapping("/create/daily")
    public String createDaily(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("kitchenTypes", recipesService.getKitchenTypes());
        return "plans/create/daily";
    }

    @GetMapping("/create/weekly")
    public String createWeekly(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("kitchenTypes", recipesService.getKitchenTypes());
        return "plans/create/weekly";
    }

    @GetMapping("/create/family")
    public String createFamily(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("kitchenTypes", recipesService.getKitchenTypes());
        return "plans/create/family";
    }

    @PostMapping("/create/daily")
    public String createDaily(HttpSession session, @RequestParam List<String> kitchenTypes, @RequestParam int maxDurationHours, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");

        Duration[] durations = new Duration[1];
        durations[0] = Duration.ofHours(maxDurationHours);

        List<Recipe> recipes = recipesService.getAll();

        DailyMealPlan plan = new DailyMealPlan();
        plan.generatePlan(kitchenTypes.toArray(new String[0]),
                durations,
                recipes.toArray(new Recipe[0]));

        int mealId = mealPlansService.add(new MealPlanModel(
                0,
                plan,
                user.getId()
        ));
        user.addMealPlan(plan,mealId);

        return "redirect:/plans/myPlans";
    }

    @PostMapping("/create/weekly")
    public String createWeekly(HttpSession session, @RequestParam List<String> kitchenTypes, @RequestParam int maxDurationHours, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");

        Duration[] durations = new Duration[1];
        durations[0] = Duration.ofHours(maxDurationHours);

        List<Recipe> recipes = recipesService.getAll();

        WeeklyMealPlan plan = new WeeklyMealPlan();
        plan.generatePlan(kitchenTypes.toArray(new String[0]),
                durations,
                recipes.toArray(new Recipe[0]));

        int mealId = mealPlansService.add(new MealPlanModel(
                0,
                plan,
                user.getId()
        ));
        user.addMealPlan(plan,mealId);

        return "redirect:/plans/myPlans";
    }

    @PostMapping("/create/family")
    public String createFamily(HttpSession session, @RequestParam List<String> kitchenTypes, @RequestParam int maxDurationHours, @RequestParam int members, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");

        Duration[] durations = new Duration[1];
        durations[0] = Duration.ofHours(maxDurationHours);

        List<Recipe> recipes = recipesService.getAll();

        FamilyMealPlan plan = new FamilyMealPlan(members);
        plan.generatePlan(kitchenTypes.toArray(new String[0]),
                durations,
                recipes.toArray(new Recipe[0]));

        int mealId = mealPlansService.add(new MealPlanModel(
                0,
                plan,
                user.getId()
        ));
        user.addMealPlan(plan,mealId);

        return "redirect:/plans/myPlans";
    }

    @GetMapping("/plan/{id}")
    public String plan(HttpSession session, @PathVariable int id, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        MealPlanModel mealPlanModel = mealPlansService.getById(id);
        mealPlanModel.mealPlan().generateGroceryList();
        model.addAttribute("plan", mealPlanModel);
        return "plans/meal-plan";
    }
}
