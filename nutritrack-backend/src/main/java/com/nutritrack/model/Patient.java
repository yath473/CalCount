package com.nutritrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a patient's full profile including demographics,
 * body measurements, medical conditions, and dietary preferences.
 */
@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---- Personal Details ----
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 120, message = "Age must be under 120")
    private Integer age;

    private String gender;       // Male, Female, Non-binary, Prefer not to say

    private String race;         // e.g. Asian, Black, Hispanic, White, etc.

    private String ethnicity;

    private String cuisinePreference; // e.g. Indian, American, Mexican, Chinese

    // ---- Body Measurements ----
    @DecimalMin(value = "50.0", message = "Height must be at least 50cm")
    @DecimalMax(value = "300.0", message = "Height must be under 300cm")
    private Double heightCm;

    @DecimalMin(value = "1.0", message = "Weight must be positive")
    private Double weightKg;

    private Double targetWeightKg;

    // BMI is calculated, not stored directly — use getCalculatedBMI()
    @Transient
    public Double getCalculatedBMI() {
        if (heightCm == null || weightKg == null || heightCm == 0) return null;
        double heightM = heightCm / 100.0;
        return Math.round((weightKg / (heightM * heightM)) * 10.0) / 10.0;
    }

    @Transient
    public String getBMICategory() {
        Double bmi = getCalculatedBMI();
        if (bmi == null) return "Unknown";
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal weight";
        if (bmi < 30.0) return "Overweight";
        return "Obese";
    }

    // ---- Medical Conditions ----
    // Stored as comma-separated string; split in service layer
    @Column(length = 1000)
    private String medicalConditions;  // e.g. "Diabetes Type 2,Hypertension"

    @Column(length = 1000)
    private String dietaryRestrictions; // e.g. "Vegetarian,Gluten-Free"

    @Column(length = 500)
    private String allergies;           // e.g. "Peanuts, Shellfish"

    @Column(length = 500)
    private String otherConditions;

    // ---- Lifestyle ----
    private String activityLevel; // sedentary, light, moderate, very, extra

    private Integer waterGoalGlasses;

    private Integer sleepGoalHours;

    private String mealPlanType;  // standard, intermittent, etc.

    @Column(length = 2000)
    private String healthcareNotes;

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

    // ---- Relationships ----
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyLog> dailyLogs;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private NutritionGoal nutritionGoal;
}
