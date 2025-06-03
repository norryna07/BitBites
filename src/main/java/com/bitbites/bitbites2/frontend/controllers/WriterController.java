package com.bitbites.bitbites2.frontend.controllers;

import com.bitbites.bitbites2.backend.RecipeDatabase;
import com.bitbites.bitbites2.backend.users.User;
import com.bitbites.bitbites2.backend.users.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.net.URL;

@Controller
@RequestMapping("/writer")
class WriterController {
    @GetMapping({"/panel", "/", ""})
    public String panel(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");
        if (user.getRole() != UserRole.WRITER) {
            model.addAttribute("error", "You are not writer");
            return "user/login";
        }
        return "writer/panel";
    }

    @PostMapping("/addRecipes")
    public String addRecipes(HttpSession session, @RequestParam String category, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");
        if (user.getRole() != UserRole.WRITER) {
            model.addAttribute("error", "You are not writer");
            return "user/login";
        }

        String urlString = RecipeDatabase.getInstance().getCategory(category);
        try {
            RecipeDatabase.getInstance().generateCategoryRecipes(
                    new URL(urlString)
            );
            model.addAttribute("success", "Recipes added");
        } catch (MalformedURLException e) {
            model.addAttribute("error", "Cannot generate recipes");
        }
        return "writer/panel";
    }

}
