package com.bitbites.bitbites2.frontend.controllers;

import com.bitbites.bitbites2.backend.services.UsersService;
import com.bitbites.bitbites2.backend.users.PasswordUtils;
import com.bitbites.bitbites2.backend.users.User;
import com.bitbites.bitbites2.backend.users.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
class UserController {
    private UsersService usersService = new UsersService();
    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/register")
    public String register() {
        return "user/register";
    }

    @PostMapping("/register")
    public String register(HttpSession session,@RequestParam String name, @RequestParam String password, Model model) {
        // first check if a user with the same name exists
        if (usersService.getByName(name) != null) {
            model.addAttribute("error", "User already exists");
            return "user/register";
        }
        usersService.add(new User(
                name,
                PasswordUtils.hashPassword(password),
                UserRole.COOKER
        ));
        if (usersService.getByName(name) == null) {
            model.addAttribute("error", "Adding failed");
            return "user/register";
        }
        model.addAttribute("success", "User has been logged in");
        session.setAttribute("user", usersService.getByName(name));
        return "user/register";
    }

    @PostMapping("/login")
    public String login(HttpSession session,@RequestParam String name, @RequestParam String password, Model model) {
        User user = usersService.getByName(name);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "user/login";
        }
        if (PasswordUtils.hashPassword(password).equals(user.getHashedPassword())) {
            model.addAttribute("success", "User has been logged in");
            session.setAttribute("user", user);
            return "redirect:/plans/myPlans";
        }
        model.addAttribute("error", "Wrong password");
        return "user/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/recipe";
    }

}
