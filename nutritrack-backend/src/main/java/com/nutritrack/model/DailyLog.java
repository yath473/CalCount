package com.nutritrack.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents one full day of food logging for a patient.
 * Contains 5 meal sessions: Breakfast, Morning Snack,
 * Lunch, Afternoon Snack, Dinner.
 */
@Entity
@Table(name = "daily_logs",
       uniqueConstraints = @UniqueConstraint(columnNames = {"patient_id", "log_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private LocalDate logDate;

    // ---- Meal entries for the day ----
    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MealEntry> mealEntries = new ArrayList<>();

    // ---- Daily totals (computed in service layer) ----
    private Integer totalCalories;
    private Double  totalProteinGrams;
    private Double  totalCarbsGrams;
    private Double  totalFatGrams;
    private Double  totalFiberGrams;

    // Was today's calorie goal met? (set by service after comparison with NutritionGoal)
    private Boolean goalMet;

    // ---- Timestamps ----
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ---- Helper: recalculate totals from all meal entries ----
    public void recalculateTotals() {
        this.totalCalories     = mealEntries.stream().mapToInt(MealEntry::getTotalCalories).sum();
        this.totalProteinGrams = mealEntries.stream().mapToDouble(MealEntry::getTotalProtein).sum();
        this.totalCarbsGrams   = mealEntries.stream().mapToDouble(MealEntry::getTotalCarbs).sum();
        this.totalFatGrams     = mealEntries.stream().mapToDouble(MealEntry::getTotalFat).sum();
        this.totalFiberGrams   = mealEntries.stream().mapToDouble(MealEntry::getTotalFiber).sum();
    }
}
