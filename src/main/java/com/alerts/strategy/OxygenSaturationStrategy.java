package com.alerts.strategy;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlert;

public class OxygenSaturationStrategy implements AlertStrategy {
    @Override
    public Alert checkAlert(String patientId, double value, long timestamp) {


        if (value > 100) {
            return new BloodOxygenAlert(patientId,
                    "Critical hypoxia: SpO2 at " + value + "%", timestamp);
        }
        if (value < 90) {
            return new BloodOxygenAlert(patientId, "Low oxygen warning: SpO2 at " + value + "%", timestamp);
        }
        return null;
    }
}
