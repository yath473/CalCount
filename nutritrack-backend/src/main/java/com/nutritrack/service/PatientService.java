package com.nutritrack.service;

import com.nutritrack.model.Patient;
import com.nutritrack.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for patient profile management.
 * Handles create, read, update, delete, and BMI calculation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    // ---- Create ----
    public Patient createPatient(Patient patient) {
        log.info("Creating new patient profile for: {}", patient.getName());
        return patientRepository.save(patient);
    }

    // ---- Read ----
    @Transactional(readOnly = true)
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Patient> getPatientsByCondition(String condition) {
        return patientRepository.findByMedicalCondition(condition);
    }

    // ---- Update ----
    public Patient updatePatient(Long id, Patient updatedData) {
        Patient existing = getPatientById(id);

        existing.setName(updatedData.getName());
        existing.setAge(updatedData.getAge());
        existing.setGender(updatedData.getGender());
        existing.setRace(updatedData.getRace());
        existing.setEthnicity(updatedData.getEthnicity());
        existing.setCuisinePreference(updatedData.getCuisinePreference());
        existing.setHeightCm(updatedData.getHeightCm());
        existing.setWeightKg(updatedData.getWeightKg());
        existing.setTargetWeightKg(updatedData.getTargetWeightKg());
        existing.setMedicalConditions(updatedData.getMedicalConditions());
        existing.setDietaryRestrictions(updatedData.getDietaryRestrictions());
        existing.setAllergies(updatedData.getAllergies());
        existing.setOtherConditions(updatedData.getOtherConditions());
        existing.setActivityLevel(updatedData.getActivityLevel());
        existing.setWaterGoalGlasses(updatedData.getWaterGoalGlasses());
        existing.setSleepGoalHours(updatedData.getSleepGoalHours());
        existing.setMealPlanType(updatedData.getMealPlanType());
        existing.setHealthcareNotes(updatedData.getHealthcareNotes());

        log.info("Updated patient profile for ID: {}", id);
        return patientRepository.save(existing);
    }

    // ---- Delete ----
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
        log.info("Deleted patient with ID: {}", id);
    }

    // ---- BMI Summary ----
    @Transactional(readOnly = true)
    public PatientBMISummary getBMISummary(Long patientId) {
        Patient p = getPatientById(patientId);
        return new PatientBMISummary(
                p.getId(),
                p.getName(),
                p.getHeightCm(),
                p.getWeightKg(),
                p.getCalculatedBMI(),
                p.getBMICategory()
        );
    }

    // ---- Inner DTO ----
    public record PatientBMISummary(
            Long   patientId,
            String name,
            Double heightCm,
            Double weightKg,
            Double bmi,
            String bmiCategory
    ) {}
}
