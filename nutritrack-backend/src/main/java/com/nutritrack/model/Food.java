package com.nutritrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Represents a single food item in the NutriTrack food database.
 * Covers multiple cuisines: American, Indian, Mexican, Chinese,
 * Japanese, Italian, Mediterranean, Korean, Thai, African, etc.
 */
@Entity
@Table(name = "foods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Food name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Cuisine category is required")
    private String category;  // American, Indian, Mexican, Chinese, Japanese, etc.

    @NotBlank(message = "Serving description is required")
    private String servingDescription; // e.g. "1 cup", "1 burger", "6 oz"

    // ---- Macronutrients (per serving) ----
    @NotNull
    @Min(0)
    private Integer caloriesPerServing;

    @Min(0)
    private Double proteinGrams;

    @Min(0)
    private Double carbsGrams;

    @Min(0)
    private Double fatGrams;

    @Min(0)
    private Double fiberGrams;

    // ---- Optional Micronutrients ----
    private Double sodiumMg;

    private Double sugarGrams;

    private Double saturatedFatGrams;

    private Double cholesterolMg;

    private Double vitaminCMg;

    private Double calciumMg;

    private Double ironMg;

    // ---- Dietary flags ----
    private Boolean isVegetarian;

    private Boolean isVegan;

    private Boolean isGlutenFree;

    private Boolean isDairyFree;

    private Boolean isHalal;

    private Boolean isKosher;

    // ---- Helper: calories from macros (cross-check) ----
    @Transient
    public Double calculateCaloriesFromMacros() {
        double p = proteinGrams != null ? proteinGrams * 4 : 0;
        double c = carbsGrams  != null ? carbsGrams  * 4 : 0;
        double f = fatGrams    != null ? fatGrams    * 9 : 0;
        return p + c + f;
    }
}
