package com.dropsett.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.ExerciseSet;
import com.dropsett.app.model.SessionExercise;
import com.dropsett.app.model.WorkoutSession;
import com.dropsett.app.ui.adapter.ExerciseAdapter;
import com.dropsett.app.ui.adapter.WorkoutExerciseAdapter;
import com.dropsett.app.util.AppExecutors;
import com.dropsett.app.util.DateUtil;
import com.dropsett.app.util.RestTimerManager;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutActivity extends AppCompatActivity {

    public static final String EXTRA_PLAN_ID        = "planId";
    public static final String EXTRA_PLAN_DAY_ID    = "planDayId";
    public static final String EXTRA_PLAN_DAY_INDEX = "planDayIndex";

    private AppDatabase db;
    private WorkoutExerciseAdapter workoutAdapter;
    private RestTimerManager restTimerManager;

    private Long planId = null;
    private Long planDayId = null;
    private int planDayIndex = 0;
    private long startTimeMillis;
    private long elapsedSeconds = 0;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private TextView tvElapsed;
    private TextView tvRestTimerLabel;
    private TextView tvClock;

    private final Runnable sessionTimerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedSeconds = (System.currentTimeMillis() - startTimeMillis) / 1000;
            if (tvElapsed != null) {
                tvElapsed.setText(formatDuration(elapsedSeconds));
            }
            timerHandler.postDelayed(this, 1000);
        }
    };

    private final Runnable clockRunnable = new Runnable() {
        @Override
        public void run() {
            if (tvClock != null) {
                tvClock.setText(new SimpleDateFormat(
                        "HH:mm", Locale.getDefault()).format(new Date()));
            }
            clockHandler.postDelayed(this, 30000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        db = AppDatabase.getInstance(this);
        restTimerManager = new RestTimerManager();

        if (getIntent().hasExtra(EXTRA_PLAN_ID)) {
            planId = getIntent().getLongExtra(EXTRA_PLAN_ID, -1);
        }
        if (getIntent().hasExtra(EXTRA_PLAN_DAY_ID)) {
            planDayId = getIntent().getLongExtra(EXTRA_PLAN_DAY_ID, -1);
        }
        planDayIndex = getIntent().getIntExtra(EXTRA_PLAN_DAY_INDEX, 0);

        tvElapsed       = findViewById(R.id.tvElapsed);
        tvRestTimerLabel = findViewById(R.id.tvRestTimerLabel);
        tvClock         = findViewById(R.id.tvWorkoutClock);

        tvClock.setText(new SimpleDateFormat(
                "HH:mm", Locale.getDefault()).format(new Date()));
        clockHandler.postDelayed(clockRunnable, 30000);

        startTimeMillis = System.currentTimeMillis();
        timerHandler.post(sessionTimerRunnable);

        setupHeader();

        RecyclerView recyclerWorkout = findViewById(R.id.recyclerWorkoutExercises);
        recyclerWorkout.setLayoutManager(new LinearLayoutManager(this));
        workoutAdapter = new WorkoutExerciseAdapter(db);
        recyclerWorkout.setAdapter(workoutAdapter);

        if (planDayId != null && planDayId != -1) {
            preloadPlanExercises();
        }

        findViewById(R.id.btnRestTimer).setOnClickListener(v -> showRestTimerDialog());
        findViewById(R.id.btnFinishWorkout).setOnClickListener(v -> showFinishDialog());
        findViewById(R.id.btnAddExercise).setOnClickListener(v -> showExercisePicker());

        if (getSupportActionBar() != null) getSupportActionBar().hide();
    }

    private void setupHeader() {
        TextView tvWorkoutTitle = findViewById(R.id.tvWorkoutTitle);
        if (planId != null && planDayId != null && planDayId != -1) {
            AppExecutors.diskIO().execute(() -> {
                com.dropsett.app.model.WorkoutPlan plan =
                        db.workoutPlanDao().getPlanById(planId);
                com.dropsett.app.model.PlanDay day =
                        db.workoutPlanDao().getDayById(planDayId);
                runOnUiThread(() -> {
                    String title = (plan != null ? plan.name : "Workout")
                            + " — Day " + (planDayIndex + 1)
                            + (day != null && day.label != null && !day.label.isEmpty()
                            ? " (" + day.label + ")" : "");
                    tvWorkoutTitle.setText(title);
                });
            });
        } else {
            tvWorkoutTitle.setText("Today's Workout");
        }
    }

    private void preloadPlanExercises() {
        AppExecutors.diskIO().execute(() -> {
            List<com.dropsett.app.model.PlanExercise> planExercises =
                    db.workoutPlanDao().getExercisesForDay(planDayId);
            for (com.dropsett.app.model.PlanExercise pe : planExercises) {
                com.dropsett.app.model.Exercise exercise =
                        db.exerciseDao().getById(pe.exerciseId);
                if (exercise != null) {
                    runOnUiThread(() ->
                            workoutAdapter.addExercise(exercise, pe.targetSets, pe.targetRpe));
                }
            }
        });
    }

    private void showRestTimerDialog() {
        if (restTimerManager.isRunning()) {
            new AlertDialog.Builder(this)
                    .setTitle("Rest Timer Running")
                    .setMessage("Cancel the current rest timer?")
                    .setPositiveButton("Cancel Timer", (d, w) -> {
                        restTimerManager.cancel();
                        tvRestTimerLabel.setText("Rest");
                    })
                    .setNegativeButton("Keep Running", null)
                    .show();
            return;
        }

        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_rest_timer, null);
        NumberPicker minutePicker = dialogView.findViewById(R.id.minutePicker);
        NumberPicker secondPicker = dialogView.findViewById(R.id.secondPicker);
        minutePicker.setMinValue(0); minutePicker.setMaxValue(10); minutePicker.setValue(2);
        secondPicker.setMinValue(0); secondPicker.setMaxValue(59); secondPicker.setValue(0);

        new AlertDialog.Builder(this)
                .setTitle("Rest Timer")
                .setView(dialogView)
                .setPositiveButton("Start", (d, w) -> {
                    long total = (minutePicker.getValue() * 60L) + secondPicker.getValue();
                    if (total > 0) startRestTimer(total);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void startRestTimer(long seconds) {
        tvRestTimerLabel.setText(formatDuration(seconds));
        restTimerManager.start(seconds, new RestTimerManager.TimerListener() {
            @Override
            public void onTick(long secondsRemaining) {
                tvRestTimerLabel.setText(formatDuration(secondsRemaining));
            }
            @Override
            public void onFinish() {
                tvRestTimerLabel.setText("Rest");
                showTimerFinishedDialog();
            }
        });
    }

    private void showTimerFinishedDialog() {
        if (isFinishing() || isDestroyed()) return;
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(
                        500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(500);
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("Rest Over!")
                .setMessage("Time to get back to it.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showExercisePicker() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_pick_exercise, null);
        RecyclerView recycler = dialogView.findViewById(R.id.recyclerPickExercise);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        ExerciseAdapter pickAdapter = new ExerciseAdapter();
        recycler.setAdapter(pickAdapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Pick Exercise")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .create();

        pickAdapter.setOnExerciseClickListener(exercise -> {
            workoutAdapter.addExercise(exercise, 1, 0);
            dialog.dismiss();
        });

        db.exerciseDao().getAllExercises().observe(this, pickAdapter::setExercises);
        dialog.show();
    }

    private void showFinishDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Finish Workout?")
                .setMessage("This will save your workout and end the session.")
                .setPositiveButton("Save", (d, w) -> saveWorkout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveWorkout() {
        timerHandler.removeCallbacks(sessionTimerRunnable);
        clockHandler.removeCallbacks(clockRunnable);
        restTimerManager.cancel();

        List<WorkoutExerciseAdapter.WorkoutExerciseItem> items =
                workoutAdapter.getExerciseItems();

        if (items.isEmpty()) {
            Toast.makeText(this, "No exercises to save", Toast.LENGTH_SHORT).show();
            return;
        }

        AppExecutors.diskIO().execute(() -> {
            WorkoutSession session = new WorkoutSession(
                    planId, planDayId != null ? planDayId.intValue() : null,
                    planDayIndex, DateUtil.today(), elapsedSeconds, "");
            long sessionId = db.sessionDao().insertSession(session);

            for (int i = 0; i < items.size(); i++) {
                WorkoutExerciseAdapter.WorkoutExerciseItem item = items.get(i);

                List<ExerciseSet> setsToSave = new ArrayList<>();
                for (ExerciseSet set : item.sets) {
                    boolean hasActual = set.actualWeight > 0 || set.actualReps > 0;
                    if (hasActual) {
                        setsToSave.add(set);
                    } else if (set.hintWeight > 0 || set.hintReps > 0) {
                        // promote hint to actual
                        set.actualWeight = set.hintWeight;
                        set.actualReps   = set.hintReps;
                        setsToSave.add(set);
                    }
                    // truly empty — drop
                }

                if (setsToSave.isEmpty()) continue;

                SessionExercise se = new SessionExercise(sessionId, item.exercise.id, i);
                long seId = db.sessionDao().insertSessionExercise(se);
                for (ExerciseSet set : setsToSave) {
                    set.sessionExerciseId = seId;
                    db.sessionDao().insertSet(set);
                }
            }

            runOnUiThread(() -> {
                Snackbar.make(findViewById(android.R.id.content),
                        "Workout saved!", Snackbar.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private String formatDuration(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        if (h > 0) return String.format("%d:%02d:%02d", h, m, s);
        return String.format("%02d:%02d", m, s);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leave Workout?")
                .setMessage("Your workout will not be saved.")
                .setPositiveButton("Leave", (d, w) -> {
                    timerHandler.removeCallbacks(sessionTimerRunnable);
                    clockHandler.removeCallbacks(clockRunnable);
                    restTimerManager.cancel();
                    finish();
                })
                .setNegativeButton("Stay", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(sessionTimerRunnable);
        clockHandler.removeCallbacks(clockRunnable);
        restTimerManager.cancel();
    }
}