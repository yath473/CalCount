package com.nutritrack.service;

import com.nutritrack.model.NutritionGoal;
import com.nutritrack.model.Patient;
import com.nutritrack.repository.NutritionGoalRepository;
import com.nutritrack.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for creating, reading, and updating
 * a patient's nutrition goals and notification preferences.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NutritionGoalService {

    private final NutritionGoalRepository nutritionGoalRepository;
    private final PatientRepository       patientRepository;

    /** Get the goal for a patient (or a default if none set). */
    @Transactional(readOnly = true)
    public NutritionGoal getGoalForPatient(Long patientId) {
        return nutritionGoalRepository.findByPatientId(patientId)
                .orElseGet(() -> NutritionGoal.fromPreset("MAINTAIN"));
    }

    /** Set or update a patient's goal from a preset type. */
    public NutritionGoal applyPreset(Long patientId, String presetType) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));

        NutritionGoal goal = nutritionGoalRepository.findByPatientId(patientId)
                .orElse(NutritionGoal.fromPreset(presetType));

        NutritionGoal preset = NutritionGoal.fromPreset(presetType);
        goal.setPatient(patient);
        goal.setGoalType(presetType);
        goal.setDailyCaloriesTarget(preset.getDailyCaloriesTarget());
        goal.setProteinGramsTarget(preset.getProteinGramsTarget());
        goal.setCarbsGramsTarget(preset.getCarbsGramsTarget());
        goal.setFatGramsTarget(preset.getFatGramsTarget());
        goal.setFiberGramsTarget(preset.getFiberGramsTarget());
        goal.setWaterGlassesTarget(preset.getWaterGlassesTarget());

        log.info("Applied preset '{}' for patient {}", presetType, patientId);
        return nutritionGoalRepository.save(goal);
    }

    /** Save a fully custom goal for a patient. */
    public NutritionGoal saveCustomGoal(Long patientId, NutritionGoal newGoal) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));

        NutritionGoal existing = nutritionGoalRepository.findByPatientId(patientId)
                .orElse(new NutritionGoal());

        existing.setPatient(patient);
        existing.setGoalType(newGoal.getGoalType() != null ? newGoal.getGoalType() : "CUSTOM");
        existing.setDailyCaloriesTarget(newGoal.getDailyCaloriesTarget());
        existing.setProteinGramsTarget(newGoal.getProteinGramsTarget());
        existing.setCarbsGramsTarget(newGoal.getCarbsGramsTarget());
        existing.setFatGramsTarget(newGoal.getFatGramsTarget());
        existing.setFiberGramsTarget(newGoal.getFiberGramsTarget());
        existing.setWaterGlassesTarget(newGoal.getWaterGlassesTarget());

        // Notification prefs
        if (newGoal.getNotifyOnCalorieExceeded()  != null) existing.setNotifyOnCalorieExceeded(newGoal.getNotifyOnCalorieExceeded());
        if (newGoal.getNotifyOnApproachingLimit() != null) existing.setNotifyOnApproachingLimit(newGoal.getNotifyOnApproachingLimit());
        if (newGoal.getNotifyOnUnderEating()      != null) existing.setNotifyOnUnderEating(newGoal.getNotifyOnUnderEating());
        if (newGoal.getNotifyOnStreakMilestone()  != null) existing.setNotifyOnStreakMilestone(newGoal.getNotifyOnStreakMilestone());
        if (newGoal.getNotifyOnMacroImbalance()   != null) existing.setNotifyOnMacroImbalance(newGoal.getNotifyOnMacroImbalance());

        log.info("Saved custom goal for patient {}", patientId);
        return nutritionGoalRepository.save(existing);
    }
}
