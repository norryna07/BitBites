package main.java.mealplans;

/**
 * Will contains the indexes from the list of Recipe from MealPlan
 * @param breakfast choice from Appetizer
 * @param lunchSoup choice from Soup
 * @param lunchMainCourse choice from MainCourse
 * @param dinnerMainCourse choice from MainCourse or Salad
 * @param dinnerDessert choice from Desert
 */
public record DailySchedule(int breakfast, int lunchSoup, int lunchMainCourse,
                            int dinnerMainCourse, int dinnerDessert) {
}
