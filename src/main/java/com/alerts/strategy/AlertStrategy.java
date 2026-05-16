package com.alerts.strategy;

import com.alerts.Alert;

public interface AlertStrategy {

    Alert checkAlert(String patientId, double reading, long timestamp);
}
