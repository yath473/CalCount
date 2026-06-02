package com.nutritrack.repository;

import com.nutritrack.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// ─────────────────────────────────────────────
// Patient Repository
// ─────────────────────────────────────────────
@Repository
interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByName(String name);

    List<Patient> findByRaceIgnoreCase(String race);

    List<Patient> findByGenderIgnoreCase(String gender);

    // Find patients with a specific medical condition (substring match)
    @Query("SELECT p FROM Patient p WHERE p.medicalConditions LIKE %:condition%")
    List<Patient> findByMedicalCondition(@Param("condition") String condition);
}

// ─────────────────────────────────────────────
// Food Repository
// ─────────────────────────────────────────────
@Repository
interface FoodRepository extends JpaRepository<Food, Long> {

    // Search by name (case-insensitive, partial match)
    List<Food> findByNameContainingIgnoreCase(String name);

    // Filter by cuisine category
    List<Food> findByCategoryIgnoreCase(String category);

    // Search by name AND category
    List<Food> findByNameContainingIgnoreCaseAndCategoryIgnoreCase(String name, String category);

    // Filter by dietary flags
    List<Food> findByIsVegetarianTrue();

    List<Food> findByIsVeganTrue();

    List<Food> findByIsGlutenFreeTrue();

    List<Food> findByIsHalalTrue();

    // Low calorie foods (under a threshold)
    List<Food> findByCaloriesPerServingLessThanEqual(Integer maxCalories);

    // High protein foods
    List<Food> findByProteinGramsGreaterThanEqual(Double minProtein);

    // All distinct categories
    @Query("SELECT DISTINCT f.category FROM Food f ORDER BY f.category")
    List<String> findAllCategories();
}

// ─────────────────────────────────────────────
// DailyLog Repository
// ─────────────────────────────────────────────
@Repository
interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

    // Get a specific day's log for a patient
    Optional<DailyLog> findByPatientIdAndLogDate(Long patientId, LocalDate logDate);

    // Get all logs for a patient (sorted newest first)
    List<DailyLog> findByPatientIdOrderByLogDateDesc(Long patientId);

    // Get logs for a patient within a date range (for weekly view)
    @Query("SELECT d FROM DailyLog d WHERE d.patient.id = :patientId AND d.logDate BETWEEN :startDate AND :endDate ORDER BY d.logDate ASC")
    List<DailyLog> findByPatientIdAndDateRange(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate
    );

    // Count how many days a patient has met their goal (for streak calculation)
    @Query("SELECT COUNT(d) FROM DailyLog d WHERE d.patient.id = :patientId AND d.goalMet = true AND d.logDate BETWEEN :startDate AND :endDate")
    Long countGoalMetDays(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate
    );

    // Average calories over a date range
    @Query("SELECT AVG(d.totalCalories) FROM DailyLog d WHERE d.patient.id = :patientId AND d.logDate BETWEEN :startDate AND :endDate")
    Double averageCaloriesInRange(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate
    );
}

// ─────────────────────────────────────────────
// MealEntry Repository
// ─────────────────────────────────────────────
@Repository
interface MealEntryRepository extends JpaRepository<MealEntry, Long> {

    // All entries for a given daily log
    List<MealEntry> findByDailyLogId(Long dailyLogId);

    // All entries for a specific meal type on a given log
    List<MealEntry> findByDailyLogIdAndMealType(Long dailyLogId, MealEntry.MealType mealType);

    // How many times a food has been logged by a patient
    @Query("SELECT COUNT(m) FROM MealEntry m WHERE m.dailyLog.patient.id = :patientId AND m.food.id = :foodId")
    Long countFoodLoggedByPatient(@Param("patientId") Long patientId, @Param("foodId") Long foodId);
}

// ─────────────────────────────────────────────
// NutritionGoal Repository
// ─────────────────────────────────────────────
@Repository
interface NutritionGoalRepository extends JpaRepository<NutritionGoal, Long> {

    Optional<NutritionGoal> findByPatientId(Long patientId);
}
