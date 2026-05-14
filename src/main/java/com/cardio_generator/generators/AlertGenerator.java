package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated alert states for patients.
 * Alerts are triggered based on a Poisson-like probability and can be resolved randomly
 */
public class AlertGenerator implements PatientDataGenerator {

    public static final Random randomGenerator = new Random();

    // Violation: Variable names must be camelCase. 
    // Correction: Changed 'AlertStates' to 'alertStates'
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Constructs an AlertGenerator with an initial state for each patient
     * * @param patientCount The total number of patients to monitor
     */
    public AlertGenerator(int patientCount) {
        this.alertStates = new boolean[patientCount + 1];
    }

    /**
     * Evaluates the alert state for a patient
     * If an alert is active, there is a 90% chance to resolve it
     * If inactive, there is a probability based on a Lambda value to trigger a new one
     * * @param patientId The unique identifier of the patient
     * @param outputStrategy The output method to record "triggered" or "resolved" states
     * @throws Exception If an error occurs during the random generation process
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double Lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-Lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
