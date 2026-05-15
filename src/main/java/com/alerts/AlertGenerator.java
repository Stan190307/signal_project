package com.alerts;

import java.util.List;
import java.util.ArrayList;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    //Changed to final, to prevent accidental reassignment
    private final DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);
        if (records.isEmpty()) return;
        
        checkBloodPressure(patient, records);
        checkBloodSaturation(patient, records);
        checkHypotensiveHypoxemia(patient, records);
        checkECG(patient, records);
        checkManualTrigger(patient, records);
    }

    //Checks pressure level. If bad -> send alert
    private void checkBloodPressure(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");
        List<PatientRecord> diastolic = filterByType(records, "DiastolicPressure");
        
        // 1. Critical Thresholds
        for (PatientRecord r : systolic) {
            if (r.getMeasurementValue() > 180 || r.getMeasurementValue() < 90) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Critical Systolic BP", r.getTimestamp()));
            }
        }

        for (PatientRecord r : diastolic) {
            if (r.getMeasurementValue() > 120 || r.getMeasurementValue() < 60) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Critical Systolic BP", r.getTimestamp()));
            }
        }

        // 2. Trend Alert (3 consecutive readings changing by > 10 mmHg)
        checkTrend(patient, systolic, "Systolic Trend");
        checkTrend(patient, diastolic, "Diastolic Trend");
        
    }

    private void checkTrend(Patient patient, List<PatientRecord> records, String condition) {
        for (int i = 2; i < records.size(); i++) {
            double v1 = records.get(i-2).getMeasurementValue();
            double v2 = records.get(i-1).getMeasurementValue();
            double v3 = records.get(i).getMeasurementValue();

            boolean increasing = (v2 - v1 > 10) && (v3 - v2 > 10);
            boolean decreasing = (v1 - v2 > 10) && (v2 - v3 > 10);

            if (increasing || decreasing) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()), condition, records.get(i).getTimestamp()));
            }
        }
    }

    //Checks saturation level. If bad -> send alert
    private void checkBloodSaturation(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> saturation = filterByType(records, "Saturation");

        for (int i = 0; i < saturation.size(); i++) {
            PatientRecord current = saturation.get(i);
            
            // 1. Low Saturation (< 92%)
            if (current.getMeasurementValue() < 92) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Low Saturation", current.getTimestamp()));
            }

            // 2. Rapid Drop (5% drop within 10 minutes)
            for (int j = 0; j < i; j++) {
                PatientRecord previous = saturation.get(j);
                long timeDiff = current.getTimestamp() - previous.getTimestamp();
                double drop = previous.getMeasurementValue() - current.getMeasurementValue();

                if (timeDiff <= 600000 && drop >= 5) { // 10 minutes = 600,000ms
                    triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Rapid Saturation Drop", current.getTimestamp()));
                    break; 
                }
            }
        }
    }

    private void checkHypotensiveHypoxemia(Patient patient, List<PatientRecord> records) {
        // Trigger if Systolic < 90 AND Saturation < 92%
        // This logic assumes evaluation happens on current data state
        List<PatientRecord> sys = filterByType(records, "SystolicPressure");
        List<PatientRecord> sat = filterByType(records, "Saturation");

        if (!sys.isEmpty() && !sat.isEmpty()) {
            PatientRecord lastSys = sys.get(sys.size() - 1);
            PatientRecord lastSat = sat.get(sat.size() - 1);

            if (lastSys.getMeasurementValue() < 90 && lastSat.getMeasurementValue() < 92) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Hypotensive Hypoxemia", lastSys.getTimestamp()));
            }
        }
    }

    private void checkECG(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> ecgRecords = filterByType(records, "ECG");
        int windowSize = 10; // Sliding window of the last 10 readings

        for (int i = 0; i < ecgRecords.size(); i++) {
            double currentVal = ecgRecords.get(i).getMeasurementValue();
        
            // Calculate the moving average of the sliding window
            double sum = 0;
            int count = 0;
            for (int j = Math.max(0, i - windowSize); j < i; j++) {//solved error for when we had less data than window size
                sum += ecgRecords.get(j).getMeasurementValue();
                count++;
            }

            if (count > 0) {
                double average = sum / count;
                // Trigger alert if current peak is "far beyond" (e.g., > 2x) the average
                if (currentVal > average * 2.0) {
                    triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Abnormal ECG Peak", ecgRecords.get(i).getTimestamp()));
                }
            }
        }
    }

    private void checkManualTrigger(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> manualAlerts = filterByType(records, "Alert");

        for (PatientRecord record : manualAlerts) {
            // If the measurement value is 1 (triggered) or represents a specific alert code
            if (record.getMeasurementValue() == 1.0) {
                triggerAlert(new Alert(
                    String.valueOf(patient.getPatientId()), 
                    "Manual Alert Triggered", 
                    record.getTimestamp()
                ));
            } else if (record.getMeasurementValue() == 0.0) {
                // Optional: Handle 'untriggered' or 'resolved' signals if necessary
                System.out.println("Manual Alert Resolved for Patient " + patient.getPatientId());
            }
        }
    }

    //This method filters the records for proper alertdetectrion
    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        List<PatientRecord> filtered = new ArrayList<>();
        for (PatientRecord r : records) {
            if (r.getRecordType().equalsIgnoreCase(type)) filtered.add(r);
        }
        return filtered;
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
        //NOT SURE WHAT TO ACTUALLY IMPLEMENT HERE (PART 3, II. SYSTEM REQUIREMENTS, 2.TRIGGERING ALERTS)
        System.out.println("ALERT TRIGGERED: " + alert.getCondition() + " for Patient " + alert.getPatientId());
    }
}
