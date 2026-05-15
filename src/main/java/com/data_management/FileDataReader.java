package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Concrete implementation of DataReader that reads simulation data from a directory.
 */
public class FileDataReader implements DataReader {
    private String outputDirectory;

    public FileDataReader(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File folder = new File(outputDirectory);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            throw new IOException("Directory not found: " + outputDirectory);
        }

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        parseAndStore(line, dataStorage);
                    }
                }
            }
        }
    }

    private void parseAndStore(String line, DataStorage storage) {
        // Example format: Patient ID: 1, Timestamp: 1714376789050, Label: ECG, Data: 0.5
        try {
            String[] parts = line.split(", ");
            int patientId = Integer.parseInt(parts[0].split(": ")[1]);
            long timestamp = Long.parseLong(parts[1].split(": ")[1]);
            String label = parts[2].split(": ")[1];
            String dataStr = parts[3].split(": ")[1].replace("%", ""); // Handle Saturation %
            double value = Double.parseDouble(dataStr);

            storage.addPatientData(patientId, value, label, timestamp);
        } catch (Exception e) {
            System.err.println("Error parsing line: " + line);
        }
    }
}
