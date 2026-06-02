package com.nutritrack.controller;

import com.nutritrack.model.DailyLog;
import com.nutritrack.model.MealEntry;
import com.nutritrack.service.DailyLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST API endpoints for daily food logging,
 * weekly progress, streaks, and notifications.
 *
 * Base URL: /api/logs
 */
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DailyLogController {

    private final DailyLogService dailyLogService;

    // ─────────────────────────────────────────────
    // Daily Log
    // ─────────────────────────────────────────────

    // GET /api/logs/{patientId}/today — Get or create today's log
    @GetMapping("/{patientId}/today")
    public ResponseEntity<DailyLog> getTodayLog(@PathVariable Long patientId) {
        return ResponseEntity.ok(dailyLogService.getOrCreateTodayLog(patientId));
    }

    // GET /api/logs/{patientId}/date/{date} — Get log for a specific date
    @GetMapping("/{patientId}/date/{date}")
    public ResponseEntity<DailyLog> getLogByDate(
            @PathVariable Long patientId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return dailyLogService.getLogByDate(patientId, date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/logs/{patientId}/all — Get all logs for a patient
    @GetMapping("/{patientId}/all")
    public ResponseEntity<List<DailyLog>> getAllLogs(@PathVariable Long patientId) {
        return ResponseEntity.ok(dailyLogService.getAllLogsForPatient(patientId));
    }

    // ─────────────────────────────────────────────
    // Meal Entries
    // ─────────────────────────────────────────────

    /**
     * POST /api/logs/{patientId}/entries — Add a food to a meal session
     *
     * Request body:
     * {
     *   "foodId": 3,
     *   "mealType": "LUNCH",
     *   "servings": 1.5
     * }
     */
    @PostMapping("/{patientId}/entries")
    public ResponseEntity<DailyLog> addMealEntry(
            @PathVariable Long patientId,
            @RequestBody AddMealEntryRequest request) {
        DailyLog updated = dailyLogService.addMealEntry(
                patientId,
                request.foodId(),
                MealEntry.MealType.valueOf(request.mealType().toUpperCase()),
                request.servings()
        );
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/logs/{patientId}/entries/{entryId} — Remove a meal entry
     */
    @DeleteMapping("/{patientId}/entries/{entryId}")
    public ResponseEntity<DailyLog> removeMealEntry(
            @PathVariable Long patientId,
            @PathVariable Long entryId) {
        return ResponseEntity.ok(dailyLogService.removeMealEntry(patientId, entryId));
    }

    // ─────────────────────────────────────────────
    // Weekly View
    // ─────────────────────────────────────────────

    // GET /api/logs/{patientId}/week — Get last 7 days of logs
    @GetMapping("/{patientId}/week")
    public ResponseEntity<DailyLogService.WeeklySummary> getWeeklySummary(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(dailyLogService.getWeeklySummary(patientId));
    }

    // GET /api/logs/{patientId}/range?start=2025-01-01&end=2025-01-31
    @GetMapping("/{patientId}/range")
    public ResponseEntity<List<DailyLog>> getLogsInRange(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(dailyLogService.getLogsForRange(patientId, start, end));
    }

    // ─────────────────────────────────────────────
    // Streak & Notifications
    // ─────────────────────────────────────────────

    // GET /api/logs/{patientId}/streak — Get current streak count
    @GetMapping("/{patientId}/streak")
    public ResponseEntity<Map<String, Integer>> getStreak(@PathVariable Long patientId) {
        int streak = dailyLogService.calculateStreak(patientId);
        return ResponseEntity.ok(Map.of("streak", streak));
    }

    // GET /api/logs/{patientId}/notifications — Get active alerts for today
    @GetMapping("/{patientId}/notifications")
    public ResponseEntity<List<String>> getNotifications(@PathVariable Long patientId) {
        return ResponseEntity.ok(dailyLogService.checkNotifications(patientId));
    }

    // ─────────────────────────────────────────────
    // Request DTO
    // ─────────────────────────────────────────────
    public record AddMealEntryRequest(Long foodId, String mealType, double servings) {}
}
