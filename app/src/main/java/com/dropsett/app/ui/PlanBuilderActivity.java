package com.dropsett.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.Exercise;
import com.dropsett.app.model.PlanDay;
import com.dropsett.app.model.PlanExercise;
import com.dropsett.app.model.WorkoutPlan;
import com.dropsett.app.ui.adapter.ExerciseAdapter;
import com.dropsett.app.util.AppExecutors;
import com.dropsett.app.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class PlanBuilderActivity extends AppCompatActivity {

    private AppDatabase db;
    private EditText etPlanName;
    private LinearLayout daysContainer;
    private int dayCount = 3;

    // Each day holds a label and a list of planned exercises
    private final List<DayEntry> dayEntries = new ArrayList<>();

    static class DayEntry {
        String label = "";
        final List<ExerciseEntry> exercises = new ArrayList<>();
    }

    static class ExerciseEntry {
        Exercise exercise;
        int sets = 3;
        int targetRpe = 0;

        ExerciseEntry(Exercise exercise) {
            this.exercise = exercise;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_builder);

        db = AppDatabase.getInstance(this);
        etPlanName = findViewById(R.id.etPlanName);
        daysContainer = findViewById(R.id.daysContainer);

        // day count picker
        NumberPicker dayPicker = findViewById(R.id.dayCountPicker);
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(7);
        dayPicker.setValue(dayCount);
        dayPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            dayCount = newVal;
            rebuildDays();
        });

        rebuildDays();

        Button btnSave = findViewById(R.id.btnSavePlan);
        btnSave.setOnClickListener(v -> savePlan());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("New Plan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void rebuildDays() {
        // preserve existing entries, grow or shrink
        while (dayEntries.size() < dayCount) dayEntries.add(new DayEntry());
        while (dayEntries.size() > dayCount) dayEntries.remove(dayEntries.size() - 1);
        renderDays();
    }

    private void renderDays() {
        daysContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < dayEntries.size(); i++) {
            final int dayIndex = i;
            DayEntry entry = dayEntries.get(i);

            View dayView = inflater.inflate(R.layout.item_plan_day_builder, daysContainer, false);
            TextView tvDayTitle = dayView.findViewById(R.id.tvDayName);
            EditText etLabel = dayView.findViewById(R.id.etDayLabel);
            LinearLayout exercisesContainer = dayView.findViewById(R.id.dayExercisesContainer);
            Button btnAddExercise = dayView.findViewById(R.id.btnAddExerciseToDay);

            tvDayTitle.setText("Day " + (i + 1));
            etLabel.setText(entry.label);
            etLabel.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) entry.label = etLabel.getText().toString().trim();
            });

            renderDayExercises(exercisesContainer, entry, inflater);

            btnAddExercise.setOnClickListener(v ->
                    showExercisePicker(dayIndex, exercisesContainer, entry, inflater));

            daysContainer.addView(dayView);
        }
    }

    private void renderDayExercises(LinearLayout container,
                                    DayEntry entry,
                                    LayoutInflater inflater) {
        container.removeAllViews();
        for (int i = 0; i < entry.exercises.size(); i++) {
            final int exIndex = i;
            ExerciseEntry ex = entry.exercises.get(i);

            View row = inflater.inflate(R.layout.item_plan_exercise_row,
                    container, false);

            TextView tvName = row.findViewById(R.id.tvPlanExerciseName);
            EditText etSets = row.findViewById(R.id.etPlanSets);
            EditText etRpe = row.findViewById(R.id.etPlanRpe);
            Button btnRemove = row.findViewById(R.id.btnRemovePlanExercise);

            tvName.setText(ex.exercise.name);
            etSets.setText(ex.sets > 0 ? String.valueOf(ex.sets) : "");
            etRpe.setText(ex.targetRpe > 0 ? String.valueOf(ex.targetRpe) : "");

            etSets.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String val = etSets.getText().toString().trim();
                    ex.sets = val.isEmpty() ? 3 : Integer.parseInt(val);
                }
            });
            etRpe.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String val = etRpe.getText().toString().trim();
                    ex.targetRpe = val.isEmpty() ? 0 : Integer.parseInt(val);
                }
            });
            btnRemove.setOnClickListener(v -> {
                entry.exercises.remove(exIndex);
                renderDayExercises(container, entry, inflater);
            });

            container.addView(row);
        }
    }

    private void showExercisePicker(int dayIndex,
                                    LinearLayout container,
                                    DayEntry entry,
                                    LayoutInflater inflater) {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_pick_exercise, null);
        RecyclerView recycler = dialogView.findViewById(R.id.recyclerPickExercise);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        ExerciseAdapter adapter = new ExerciseAdapter();
        recycler.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Exercise to Day " + (dayIndex + 1))
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .create();

        adapter.setOnExerciseClickListener(exercise -> {
            entry.exercises.add(new ExerciseEntry(exercise));
            renderDayExercises(container, entry, inflater);
            dialog.dismiss();
        });

        db.exerciseDao().getAllExercises().observe(this, adapter::setExercises);
        dialog.show();
    }

    private void savePlan() {
        String name = etPlanName.getText().toString().trim();
        if (name.isEmpty()) {
            etPlanName.setError("Plan name is required");
            etPlanName.requestFocus();
            return;
        }

        // flush any focused label edits
        for (int i = 0; i < daysContainer.getChildCount(); i++) {
            View dayView = daysContainer.getChildAt(i);
            EditText etLabel = dayView.findViewById(R.id.etDayLabel);
            if (etLabel != null)
                dayEntries.get(i).label = etLabel.getText().toString().trim();
        }

        AppExecutors.diskIO().execute(() -> {
            WorkoutPlan plan = new WorkoutPlan(name, DateUtil.today());
            long planId = db.workoutPlanDao().insertPlan(plan);

            for (int i = 0; i < dayEntries.size(); i++) {
                DayEntry entry = dayEntries.get(i);
                String label = entry.label.isEmpty() ? "Day " + (i + 1) : entry.label;
                PlanDay day = new PlanDay(planId, i, label);
                long dayId = db.workoutPlanDao().insertDay(day);

                for (int j = 0; j < entry.exercises.size(); j++) {
                    ExerciseEntry ex = entry.exercises.get(j);
                    PlanExercise pe = new PlanExercise(
                            dayId, ex.exercise.id,
                            ex.sets, ex.targetRpe, j);
                    db.workoutPlanDao().insertPlanExercise(pe);
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Plan saved!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}