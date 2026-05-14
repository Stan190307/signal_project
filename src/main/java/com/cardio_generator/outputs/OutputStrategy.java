package com.cardio_generator.outputs;

/**
 * Interface for defining how simulated health data should be outputted.
 * This allows for flexible data handling, such as printing to console or saving to files.
 */
public interface OutputStrategy {
    /**
     * Outputs a specific measurement or record for a patient. 
     * * @param patientId The unique identifier of the patient. 
     * @param timestamp The time in milliseconds when the data was generated. 
     * @param label The type of data being recorded (e.g., "Saturation", "Alert"). 
     * @param data The actual value or state recorded. 
     */
    void output(int patientId, long timestamp, String label, String data);
}
