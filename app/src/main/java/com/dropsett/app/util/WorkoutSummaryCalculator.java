package com.dropsett.app.util;

import com.dropsett.app.model.Exercise;
import com.dropsett.app.model.ExerciseSet;
import com.dropsett.app.model.SessionExercise;

import java.util.ArrayList;
import java.util.List;

public class WorkoutSummaryCalculator {

    public static class PrResult {
        public Exercise exercise;
        public float newWeight;
        public int newReps;
        public String description; // e.g. "100kg × 5 — new best weight"

        public PrResult(Exercise exercise, float newWeight,
                        int newReps, String description) {
            this.exercise    = exercise;
            this.newWeight   = newWeight;
            this.newReps     = newReps;
            this.description = description;
        }
    }

    public static class Summary {
        public float totalVolumeKg;   // sum of weight × reps across all sets
        public int totalSets;
        public int totalReps;
        public long durationSeconds;
        public int weeklySessionCount;
        public List<PrResult> prs = new ArrayList<>();
    }

    public static float calculateVolume(List<ExerciseSet> sets) {
        float total = 0f;
        for (ExerciseSet set : sets) {
            total += set.actualWeight * set.actualReps;
        }
        return total;
    }

    public static int calculateTotalSets(List<ExerciseSet> sets) {
        return sets.size();
    }

    public static int calculateTotalReps(List<ExerciseSet> sets) {
        int total = 0;
        for (ExerciseSet set : sets) {
            total += set.actualReps;
        }
        return total;
    }

    public static String formatVolume(float kg) {
        if (kg >= 1000) {
            return String.format("%.1f t", kg / 1000f);
        }
        return String.format("%.1f kg", kg);
    }

    public static String formatDuration(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        if (h > 0) return String.format("%dh %02dm", h, m);
        if (m > 0) return String.format("%dm %02ds", m, s);
        return String.format("%ds", s);
    }
}