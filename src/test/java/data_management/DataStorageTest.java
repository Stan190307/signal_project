package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.DataReader;
import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.alerts.AlertGenerator;


import java.io.IOException;
import java.util.List;

class DataStorageTest {
    @Test
    void testAddAndGetRecords() {
        // DataReader reader
        DataReader reader = new MockDataReader();
        DataStorage storage = new DataStorage(reader);
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    }

    @Test
    void testFileDataReaderParsing() throws IOException {
        // Setup a temporary directory or mock file
        String testDir = "test_data";
        FileDataReader reader = new FileDataReader(testDir);
        DataStorage storage = new DataStorage(reader);

        reader.readData(storage);
    
        // Check if data was accurately passed into storage
        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertFalse(records.isEmpty(), "Records should be loaded from the files");
    }

    @Test
    void testBloodPressureTrendAlert() {
        DataStorage storage = new DataStorage(null);
        AlertGenerator generator = new AlertGenerator(storage);
    
        // Add three consecutive readings with > 10mmHg increase
        storage.addPatientData(1, 120.0, "Systolic", 1000L);
        storage.addPatientData(1, 135.0, "Systolic", 2000L);
        storage.addPatientData(1, 150.0, "Systolic", 3000L);
    
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);
    
        // Logic: Verify that a trend alert was triggered
    }

    @Test
    void testBloodSaturationAlerts() {
        DataStorage storage = new DataStorage(null);
        AlertGenerator generator = new AlertGenerator(storage);
    
        // Edge Case: Drop of exactly 5% in 10 minutes
        storage.addPatientData(2, 98.0, "Saturation", 1000L);
        storage.addPatientData(2, 93.0, "Saturation", 1000L + (10 * 60 * 1000));
    
        generator.evaluateData(storage.getAllPatients().get(0));
        // Logic: Verify Rapid Drop Alert
    }

    @Test
    void testHypotensiveHypoxemiaAlert() {
        DataStorage storage = new DataStorage(null);
        AlertGenerator generator = new AlertGenerator(storage);
        int patientId = 1;

        // Condition 1: Both criteria met (Should Trigger)
        storage.addPatientData(patientId, 85.0, "SystolicPressure", System.currentTimeMillis());
        storage.addPatientData(patientId, 90.0, "Saturation", System.currentTimeMillis());
    
        // Condition 2: Only Systolic is low (Should NOT Trigger)
        int patientId2 = 2;
        storage.addPatientData(patientId2, 85.0, "SystolicPressure", System.currentTimeMillis());
        storage.addPatientData(patientId2, 95.0, "Saturation", System.currentTimeMillis());

        generator.evaluateData(storage.getPatient(patientId));
        generator.evaluateData(storage.getPatient(patientId2));
    }

    @Test
    void testECGAbnormalPeakAlert() {
        DataStorage storage = new DataStorage(null);
        AlertGenerator generator = new AlertGenerator(storage);
        int patientId = 3;

        // Add normal base readings (Average approx 0.5)
        for (int i = 0; i < 10; i++) {
            storage.addPatientData(patientId, 0.5, "ECG", System.currentTimeMillis() + i);
        }

        // Add a peak "far beyond" the average (e.g., 2.0 is 4x the average)
        storage.addPatientData(patientId, 2.0, "ECG", System.currentTimeMillis() + 11);

        generator.evaluateData(storage.getPatient(patientId));
        // Verify "Abnormal ECG Peak" is triggered
    }

    @Test
    void testManualTriggerAlert() {
        DataStorage storage = new DataStorage(null);
        AlertGenerator generator = new AlertGenerator(storage);
        int patientId = 4;

        // Test Triggered (Value 1.0)
        storage.addPatientData(patientId, 1.0, "Alert", System.currentTimeMillis());
    
        // Test Resolved/Untriggered (Value 0.0)
        storage.addPatientData(patientId, 0.0, "Alert", System.currentTimeMillis() + 1000);

        generator.evaluateData(storage.getPatient(patientId));
        // Verify "Manual Alert Triggered" appears in your output
    }
}
