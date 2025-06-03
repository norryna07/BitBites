package com.bitbites.bitbites2.backend.users;

import com.bitbites.bitbites2.backend.mealplans.MealPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String username;
    private String hashedPassword;
    private UserRole role;
    private Map<Integer, MealPlan> mealPlans;
    private int id;

    public User(String username, String hashedPassword, UserRole role) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
        mealPlans = new HashMap<Integer, MealPlan>();
    }

    public User(int id, String username, String hashedPassword, UserRole role) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
        mealPlans = new HashMap<>();
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    public void addMealPlan(MealPlan mealPlan, int id) {
        mealPlans.put(id, mealPlan);
    }

    public Map<Integer, MealPlan> getMealPlans() {
        return mealPlans;
    }

    public MealPlan getMealPlan(int id) {
        return mealPlans.get(id);
    }

    public void deleteMealPlan(int index) {
        mealPlans.remove(index);
    }

    public void setMealPlans(Map<Integer, MealPlan> mealPlans) {
        this.mealPlans = mealPlans;
    }

    public UserRole getRole() {
        return role;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return "User: " + username + " (" + role.toString().toLowerCase() + ")";
    }
}
