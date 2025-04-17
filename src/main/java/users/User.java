package main.java.users;

import main.java.mealplans.MealPlan;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String username;
    private final String hashedPassword;
    private List<MealPlan> mealPlans;

    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        mealPlans = new ArrayList<>();
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
}
