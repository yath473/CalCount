package com.nutritrack.controller;

import com.nutritrack.model.Patient;
import com.nutritrack.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API endpoints for patient profile management.
 *
 * Base URL: /api/patients
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")  // Allow frontend to call this API
public class PatientController {

    private final PatientService patientService;

    // POST /api/patients — Create a new patient profile
    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        Patient created = patientService.createPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/patients — Get all patients
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // GET /api/patients/{id} — Get one patient by ID
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    // PUT /api/patients/{id} — Update a patient's profile
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id,
                                                  @Valid @RequestBody Patient patient) {
        return ResponseEntity.ok(patientService.updatePatient(id, patient));
    }

    // DELETE /api/patients/{id} — Delete a patient
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/patients/{id}/bmi — Get BMI summary for a patient
    @GetMapping("/{id}/bmi")
    public ResponseEntity<PatientService.PatientBMISummary> getBMI(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getBMISummary(id));
    }

    // GET /api/patients/condition/{condition} — Find patients by medical condition
    @GetMapping("/condition/{condition}")
    public ResponseEntity<List<Patient>> getByCondition(@PathVariable String condition) {
        return ResponseEntity.ok(patientService.getPatientsByCondition(condition));
    }
}
