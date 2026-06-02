package com.nutritrack.service;

import com.nutritrack.model.*;
import com.nutritrack.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Core business logic for daily food logging, meal entries,
 * weekly summaries, and streak calculations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DailyLogService {

    private final DailyLogRepository      dailyLogRepository;
    private final MealEntryRepository     mealEntryRepository;
    private final PatientRepository       patientRepository;
    private final FoodRepository          foodRepository;
    private final NutritionGoalRepository nutritionGoalRepository;

    // ─────────────────────────────────────────────
    // Daily Log Management
    // ─────────────────────────────────────────────

    /** Get or create today's log for a patient. */
    public DailyLog getOrCreateTodayLog(Long patientId) {
        LocalDate today = LocalDate.now();
        return dailyLogRepository.findByPatientIdAndLogDate(patientId, today)
                .orElseGet(() -> {
                    Patient patient = patientRepository.findById(patientId)
                            .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));
                    DailyLog newLog = DailyLog.builder()
                            .patient(patient)
                            .logDate(today)
                            .totalCalories(0)
                            .totalProteinGrams(0.0)
                            .totalCarbsGrams(0.0)
                            .totalFatGrams(0.0)
                            .totalFiberGrams(0.0)
                            .goalMet(false)
                            .build();
                    log.info("Created new daily log for patient {} on {}", patientId, today);
                    return dailyLogRepository.save(newLog);
                });
    }

    /** Get a specific date's log for a patient. */
    @Transactional(readOnly = true)
    public Optional<DailyLog> getLogByDate(Long patientId, LocalDate date) {
        return dailyLogRepository.findByPatientIdAndLogDate(patientId, date);
    }

    /** Get all logs for a patient. */
    @Transactional(readOnly = true)
    public List<DailyLog> getAllLogsForPatient(Long patientId) {
        return dailyLogRepository.findByPatientIdOrderByLogDateDesc(patientId);
    }

    // ─────────────────────────────────────────────
    // Meal Entry Management
    // ─────────────────────────────────────────────

    /** Add a food item to a meal session for today. */
    public DailyLog addMealEntry(Long patientId, Long foodId,
                                  MealEntry.MealType mealType, double servings) {
        DailyLog log = getOrCreateTodayLog(patientId);
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found: " + foodId));

        MealEntry entry = MealEntry.builder()
                .dailyLog(log)
                .food(food)
                .mealType(mealType)
                .servingsConsumed(servings)
                .build();

        mealEntryRepository.save(entry);
        log.getMealEntries().add(entry);
        log.recalculateTotals();
        updateGoalMetStatus(log, patientId);
        return dailyLogRepository.save(log);
    }

    /** Remove a meal entry by its ID. */
    public DailyLog removeMealEntry(Long patientId, Long mealEntryId) {
        DailyLog log = getOrCreateTodayLog(patientId);
        MealEntry entry = mealEntryRepository.findById(mealEntryId)
                .orElseThrow(() -> new RuntimeException("Meal entry not found: " + mealEntryId));
        log.getMealEntries().remove(entry);
        mealEntryRepository.delete(entry);
        log.recalculateTotals();
        updateGoalMetStatus(log, patientId);
        return dailyLogRepository.save(log);
    }

    // ─────────────────────────────────────────────
    // Weekly View & Streak
    // ─────────────────────────────────────────────

    /** Get the last 7 days of logs for a patient. */
    @Transactional(readOnly = true)
    public List<DailyLog> getWeeklyLogs(Long patientId) {
        LocalDate endDate   = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        return dailyLogRepository.findByPatientIdAndDateRange(patientId, startDate, endDate);
    }

    /** Get the last N days of logs. */
    @Transactional(readOnly = true)
    public List<DailyLog> getLogsForRange(Long patientId, LocalDate startDate, LocalDate endDate) {
        return dailyLogRepository.findByPatientIdAndDateRange(patientId, startDate, endDate);
    }

    /** Calculate current streak — consecutive days where goal was met. */
    @Transactional(readOnly = true)
    public int calculateStreak(Long patientId) {
        List<DailyLog> recentLogs = dailyLogRepository.findByPatientIdOrderByLogDateDesc(patientId);
        int streak = 0;
        LocalDate expected = LocalDate.now();

        for (DailyLog log : recentLogs) {
            if (!log.getLogDate().equals(expected)) break;   // gap in dates
            if (Boolean.TRUE.equals(log.getGoalMet())) {
                streak++;
                expected = expected.minusDays(1);
            } else {
                break; // streak broken
            }
        }
        return streak;
    }

    /** Weekly summary DTO. */
    @Transactional(readOnly = true)
    public WeeklySummary getWeeklySummary(Long patientId) {
        List<DailyLog> weekLogs = getWeeklyLogs(patientId);
        int totalCalories = weekLogs.stream()
                .filter(l -> l.getTotalCalories() != null)
                .mapToInt(DailyLog::getTotalCalories).sum();
        long daysLogged  = weekLogs.size();
        long goalsMetCount = weekLogs.stream().filter(l -> Boolean.TRUE.equals(l.getGoalMet())).count();
        int avgCalories  = daysLogged > 0 ? (int)(totalCalories / daysLogged) : 0;
        int streak       = calculateStreak(patientId);

        return new WeeklySummary(totalCalories, avgCalories, (int) daysLogged, (int) goalsMetCount, streak, weekLogs);
    }

    // ─────────────────────────────────────────────
    // Notification Triggers
    // ─────────────────────────────────────────────

    /** Check which notifications should fire given current log state. */
    @Transactional(readOnly = true)
    public List<String> checkNotifications(Long patientId) {
        DailyLog log = getOrCreateTodayLog(patientId);
        NutritionGoal goal = nutritionGoalRepository.findByPatientId(patientId).orElse(null);
        if (goal == null) return List.of();

        List<String> alerts = new java.util.ArrayList<>();
        int consumed = log.getTotalCalories() != null ? log.getTotalCalories() : 0;
        int target   = goal.getDailyCaloriesTarget();
        double ratio = (double) consumed / target;

        if (Boolean.TRUE.equals(goal.getNotifyOnCalorieExceeded()) && ratio > 1.0) {
            alerts.add("DANGER: Calorie limit exceeded! " + consumed + " / " + target + " kcal consumed.");
        } else if (Boolean.TRUE.equals(goal.getNotifyOnApproachingLimit()) && ratio >= 0.9 && ratio <= 1.0) {
            alerts.add("WARNING: You are at " + (int)(ratio * 100) + "% of your calorie goal.");
        }
        if (Boolean.TRUE.equals(goal.getNotifyOnUnderEating()) && ratio < 0.5 && consumed > 0) {
            alerts.add("INFO: Only " + (int)(ratio * 100) + "% of calorie goal consumed. Eat more!");
        }
        if (Boolean.TRUE.equals(goal.getNotifyOnMacroImbalance()) && goal.getProteinGramsTarget() != null) {
            double proteinRatio = log.getTotalProteinGrams() / goal.getProteinGramsTarget();
            if (proteinRatio > 1.5) {
                alerts.add("WARNING: Protein intake is very high: " + log.getTotalProteinGrams() + "g vs " + goal.getProteinGramsTarget() + "g target.");
            }
        }
        if (Boolean.TRUE.equals(goal.getNotifyOnStreakMilestone())) {
            int streak = calculateStreak(patientId);
            if (streak > 0 && streak % 7 == 0) {
                alerts.add("SUCCESS: " + streak + "-day streak achieved! Keep it up!");
            }
        }
        return alerts;
    }

    // ─────────────────────────────────────────────
    // Private Helpers
    // ─────────────────────────────────────────────

    private void updateGoalMetStatus(DailyLog log, Long patientId) {
        nutritionGoalRepository.findByPatientId(patientId).ifPresent(goal -> {
            int consumed = log.getTotalCalories() != null ? log.getTotalCalories() : 0;
            int target   = goal.getDailyCaloriesTarget();
            // Goal met = within 80–120% of calorie target
            boolean met = consumed >= target * 0.8 && consumed <= target * 1.2;
            log.setGoalMet(met);
        });
    }

    // ─────────────────────────────────────────────
    // Inner DTOs
    // ─────────────────────────────────────────────

    public record WeeklySummary(
            int           totalCalories,
            int           avgDailyCalories,
            int           daysLogged,
            int           goalsMetCount,
            int           currentStreak,
            List<DailyLog> dailyLogs
    ) {}
}
