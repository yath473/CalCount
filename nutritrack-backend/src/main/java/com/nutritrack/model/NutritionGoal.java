package com.nutritrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Stores a patient's personalized daily nutrition targets.
 * One-to-one relationship with Patient.
 */
@Entity
@Table(name = "nutrition_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;

    // ---- Goal type preset ----
    // LOSE_WEIGHT, MAINTAIN, GAIN_WEIGHT, MUSCLE_BUILDING, CUSTOM
    private String goalType;

    // ---- Daily Targets ----
    @NotNull
    @Min(value = 800, message = "Minimum safe calorie goal is 800 kcal")
    @Max(value = 5000, message = "Maximum calorie goal is 5000 kcal")
    private Integer dailyCaloriesTarget;

    @Min(0)
    private Integer proteinGramsTarget;

    @Min(0)
    private Integer carbsGramsTarget;

    @Min(0)
    private Integer fatGramsTarget;

    @Min(0)
    private Integer fiberGramsTarget;

    @Min(0)
    private Integer waterGlassesTarget;

    // ---- Notification preferences ----
    private Boolean notifyOnCalorieExceeded;   // alert if over daily limit

    private Boolean notifyOnApproachingLimit;  // alert at 90% of limit

    private Boolean notifyOnUnderEating;       // alert if under 50% by end of day

    private Boolean notifyOnStreakMilestone;   // alert on streak achievements

    private Boolean notifyOnMacroImbalance;    // alert if macros are way off

    // ---- Timestamps ----
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Default all notifications to true
        if (notifyOnCalorieExceeded  == null) notifyOnCalorieExceeded  = true;
        if (notifyOnApproachingLimit == null) notifyOnApproachingLimit = true;
        if (notifyOnUnderEating      == null) notifyOnUnderEating      = true;
        if (notifyOnStreakMilestone   == null) notifyOnStreakMilestone   = true;
        if (notifyOnMacroImbalance   == null) notifyOnMacroImbalance   = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ---- Preset loader ----
    public static NutritionGoal fromPreset(String goalType) {
        return switch (goalType.toUpperCase()) {
            case "LOSE_WEIGHT"     -> NutritionGoal.builder().goalType(goalType).dailyCaloriesTarget(1500).proteinGramsTarget(100).carbsGramsTarget(150).fatGramsTarget(50).fiberGramsTarget(30).waterGlassesTarget(8).build();
            case "GAIN_WEIGHT"     -> NutritionGoal.builder().goalType(goalType).dailyCaloriesTarget(2800).proteinGramsTarget(140).carbsGramsTarget(350).fatGramsTarget(90).fiberGramsTarget(35).waterGlassesTarget(10).build();
            case "MUSCLE_BUILDING" -> NutritionGoal.builder().goalType(goalType).dailyCaloriesTarget(2500).proteinGramsTarget(180).carbsGramsTarget(280).fatGramsTarget(70).fiberGramsTarget(30).waterGlassesTarget(10).build();
            default                -> NutritionGoal.builder().goalType("MAINTAIN").dailyCaloriesTarget(2000).proteinGramsTarget(50).carbsGramsTarget(250).fatGramsTarget(65).fiberGramsTarget(25).waterGlassesTarget(8).build();
        };
    }
}
