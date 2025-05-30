package main.java.users;

import main.java.mealplans.MealPlan;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String hashedPassword;
    private UserRole role;
    private List<MealPlan> mealPlans;
    private int id;

    public User(String username, String hashedPassword, UserRole role) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
        mealPlans = new ArrayList<>();
    }

    public User(int id, String username, String hashedPassword, UserRole role) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
        mealPlans = new ArrayList<>();
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

    public void addMealPlan(MealPlan mealPlan) {
        mealPlans.add(mealPlan);
    }

    public List<MealPlan> getMealPlans() {
        return mealPlans;
    }

    public MealPlan getMealPlan(int id) {
        return mealPlans.get(id);
    }

    public void deleteMealPlan(int index) {
        mealPlans.remove(index);
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
