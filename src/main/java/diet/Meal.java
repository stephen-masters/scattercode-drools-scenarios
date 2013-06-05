package diet;

public class Meal {

    private int calories;
    private int dietClubPoints;

    public Meal() {
    }

    public Meal(int calories, int dietClubPoints) {
        this.calories = calories;
        this.dietClubPoints = dietClubPoints;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getDietClubPoints() {
        return dietClubPoints;
    }

    public void setDietClubPoints(int dietClubPoints) {
        this.dietClubPoints = dietClubPoints;
    }

}
