package com.cardio_generator;

import com.data_management.DataStorage;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            DataStorage.main(new String[]{});
        } else {
            try {
                HealthDataSimulator.main(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}