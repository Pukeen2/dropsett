package com.dropsett.app.util;

public class InputValidator {

    public static boolean isValidRpe(String input) {
        if (input == null || input.trim().isEmpty()) return true; // optional
        try {
            int val = Integer.parseInt(input.trim());
            return val >= 1 && val <= 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidWeight(String input) {
        if (input == null || input.trim().isEmpty()) return true; // optional
        try {
            float val = Float.parseFloat(input.trim());
            return val >= 0 && val <= 999;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidReps(String input) {
        if (input == null || input.trim().isEmpty()) return true; // optional
        try {
            int val = Integer.parseInt(input.trim());
            return val >= 0 && val <= 999;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}