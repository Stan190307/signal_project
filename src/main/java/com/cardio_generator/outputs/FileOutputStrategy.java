package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link OutputStrategy} that persists patient data to text files.
 * Each data label (e.g., "ECG", "Saturation") is stored in its own dedicated file 
 * within a specified base directory
 */
public class FileOutputStrategy implements OutputStrategy {

    // Changed variable name to camelCase
    private String baseDirectory;

    //Changed to private, because of encapsulation
    private final ConcurrentHashMap<String, String> file_map = new ConcurrentHashMap<>();
    //added Javadoc
    /**
     * Constructs a new FileOutputStrategy with a target directory
     * * @param baseDirectory The path to the directory where output files will be created
     */
    public FileOutputStrategy(String baseDirectory) {
        // Changed variable name to camelCase
        this.baseDirectory = baseDirectory;
    }

    /**
     * Writes a patient record to a label-specific text file
     * If the base directory does not exist, it attempts to create it
     * * @param patientId The unique identifier of the patient
     * @param timestamp The time in milliseconds when the measurement occurred
     * @param label The type of data, used as the filename (e.g., "HeartRate")
     * @param data The measurement value to be recorded
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            // Changed variable name to camelCase
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        // Changed variable name to camelCase
        String FilePath = file_map.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(FilePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + FilePath + ": " + e.getMessage());
        }
    }
}