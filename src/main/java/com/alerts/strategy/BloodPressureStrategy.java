package com.alerts.strategy;

import com.alerts.Alert;
import com.alerts.BloodPressureAlert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

public class BloodPressureStrategy implements AlertStrategy{
    private static final double HIGHBLOODPRESSURE = 130.0;
    private static final double LOWBLOODPRESSURE  = 90.0;

    @Override
    public Alert checkAlert(String patientId, double value, long timestamp) {
        // value is the systolic reading
        if (value > HIGHBLOODPRESSURE) {
            return new BloodPressureAlert(patientId, "High blood pressure: systolic " + value + " mmHg", timestamp);
        }
        if (value < LOWBLOODPRESSURE) {
            return new BloodPressureAlert(patientId, "Low blood pressure: systolic " + value + " mmHg", timestamp);
        }
        return null;
    }

    public Alert checkAlert(Patient patient, long startTime, long endTime) {
        List<PatientRecord> records = patient.getRecords(startTime, endTime);
        double systolic  = 0;
        double diastolic = 0;

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("SystolicPressure")) {
                systolic = record.getMeasurementValue();
            } else if (record.getRecordType().equals("DiastolicPressure")) {
                diastolic = record.getMeasurementValue();
            }
        }

        if (systolic > HIGHBLOODPRESSURE) {
            return new BloodPressureAlert(String.valueOf(patient.getPatientId()),
                    "High blood pressure: systolic " + systolic + " mmHg", endTime);
        }
        if (systolic < LOWBLOODPRESSURE && systolic != -1) {
            return new BloodPressureAlert(String.valueOf(patient.getPatientId()),
                    "Low blood pressure: systolic " + systolic + " mmHg", endTime);
        }

        return null;
    }

}
