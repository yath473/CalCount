package com.nutritrack.controller;

import com.nutritrack.model.NutritionGoal;
import com.nutritrack.service.NutritionGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API endpoints for managing patient nutrition goals.
 *
 * Base URL: /api/goals
 */
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NutritionGoalController {

    private final NutritionGoalService nutritionGoalService;

    // GET /api/goals/{patientId} — Get a patient's current goal
    @GetMapping("/{patientId}")
    public ResponseEntity<NutritionGoal> getGoal(@PathVariable Long patientId) {
        return ResponseEntity.ok(nutritionGoalService.getGoalForPatient(patientId));
    }

    // POST /api/goals/{patientId}/preset/{type} — Apply a preset goal
    // Types: LOSE_WEIGHT, MAINTAIN, GAIN_WEIGHT, MUSCLE_BUILDING
    @PostMapping("/{patientId}/preset/{type}")
    public ResponseEntity<NutritionGoal> applyPreset(
            @PathVariable Long patientId,
            @PathVariable String type) {
        return ResponseEntity.ok(nutritionGoalService.applyPreset(patientId, type));
    }

    // PUT /api/goals/{patientId} — Save a custom goal
    @PutMapping("/{patientId}")
    public ResponseEntity<NutritionGoal> saveGoal(
            @PathVariable Long patientId,
            @Valid @RequestBody NutritionGoal goal) {
        return ResponseEntity.ok(nutritionGoalService.saveCustomGoal(patientId, goal));
    }
}
