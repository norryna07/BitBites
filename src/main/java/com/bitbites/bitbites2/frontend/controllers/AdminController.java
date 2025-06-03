package com.bitbites.bitbites2.frontend.controllers;

import com.bitbites.bitbites2.backend.repositories.UsersRepository;
import com.bitbites.bitbites2.backend.services.MealPlansService;
import com.bitbites.bitbites2.backend.services.RecipesService;
import com.bitbites.bitbites2.backend.services.UsersService;
import com.bitbites.bitbites2.backend.users.PasswordUtils;
import com.bitbites.bitbites2.backend.users.User;
import com.bitbites.bitbites2.backend.users.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private UsersService usersService;

    public AdminController() {
        this.usersService = new UsersService();
    }

    @GetMapping("/panel")
    public String adminPanel(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");
        if (user.getRole() != UserRole.ADMIN) {
            model.addAttribute("error", "You are not admin");
            return "user/login";
        }
        List<User> users = usersService.getAll();
        model.addAttribute("users", users);
        model.addAttribute("roles", UserRole.values());
        return "admin/panel";
    }

    @PostMapping("/users/update")
    public String updateUser(HttpSession session,
                             @RequestParam Long id,
                             @RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String role, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");
        if (user.getRole() != UserRole.ADMIN) {
            model.addAttribute("error", "You are not admin");
            return "user/login";
        }
        User modifiedUser = usersService.getById(id.intValue());
        if (modifiedUser == null) {
            model.addAttribute("error", "User not found");
            return "admin/panel";
        }
        String hashedPass = PasswordUtils.hashPassword(password);
        usersService.update(new User(
                modifiedUser.getId(),
                username,
                hashedPass,
                UserRole.valueOf(role.toUpperCase())
        ));
        return "redirect:/admin/panel";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(HttpSession session, @PathVariable Long id, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("error", "You are not logged in");
            return "user/login";
        }
        User user = (User) session.getAttribute("user");
        if (user.getRole() != UserRole.ADMIN) {
            model.addAttribute("error", "You are not admin");
            return "user/login";
        }
        usersService.deleteById(id.intValue());
        return "redirect:/admin/panel";
    }
}

