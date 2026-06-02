package com.nutritrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * A single food entry within a meal session (e.g. 1.5 servings of Butter Chicken at Lunch).
 * Belongs to a DailyLog.
 */
@Entity
@Table(name = "meal_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_log_id", nullable = false)
    private DailyLog dailyLog;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    /**
     * Which meal this entry belongs to.
     * Values: BREAKFAST, MORNING_SNACK, LUNCH, AFTERNOON_SNACK, DINNER
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;

    @NotNull
    @DecimalMin(value = "0.1", message = "Quantity must be at least 0.1 servings")
    private Double servingsConsumed;

    // ---- Computed totals for this entry (servings × food macros) ----
    public int getTotalCalories() {
        return (int) Math.round(food.getCaloriesPerServing() * servingsConsumed);
    }

    public double getTotalProtein() {
        return round(food.getProteinGrams() * servingsConsumed);
    }

    public double getTotalCarbs() {
        return round(food.getCarbsGrams() * servingsConsumed);
    }

    public double getTotalFat() {
        return round(food.getFatGrams() * servingsConsumed);
    }

    public double getTotalFiber() {
        return round(food.getFiberGrams() * servingsConsumed);
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    // ---- Meal type enum ----
    public enum MealType {
        BREAKFAST,
        MORNING_SNACK,
        LUNCH,
        AFTERNOON_SNACK,
        DINNER
    }
}
