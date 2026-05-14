package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface defining the contract for classes that generate health-related data for patients.
 * Implementations should simulate specific vital signs or alerts. 
 */
public interface PatientDataGenerator {
    /**
     * Generates a single data point for a specific patient and sends it to the output strategy.
     * * @param patientId The unique identifier of the patient.
     * @param outputStrategy The strategy used to handle the generated data (e.g., Console, File).
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
