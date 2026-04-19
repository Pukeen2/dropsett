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
import com.dropsett.app.model.Exercise;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutActivity extends AppCompatActivity {

    public static final String EXTRA_PLAN_ID = "planId";
    public static final String EXTRA_PLAN_DAY_ID = "planDayId";
    public static final String EXTRA_PLAN_DAY_INDEX = "planDayIndex";

    private AppDatabase db;
    private WorkoutExerciseAdapter workoutAdapter;
    private RestTimerManager restTimerManager;

    private Long planId = null;
    private Integer planDayId = null;
    private int planDayIndex = 0;
    private long startTimeMillis;
    private long elapsedSeconds = 0;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private TextView tvRestTimer;
    private TextView tvClock;

    private final Runnable sessionTimerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedSeconds = (System.currentTimeMillis() - startTimeMillis) / 1000;
            timerHandler.postDelayed(this, 1000);
        }
    };

    private final Runnable clockRunnable = new Runnable() {
        @Override
        public void run() {
            if (tvClock != null) {
                tvClock.setText(new SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(new Date()));
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
            planDayId = (int) getIntent().getLongExtra(EXTRA_PLAN_DAY_ID, -1);
        }
        planDayIndex = getIntent().getIntExtra(EXTRA_PLAN_DAY_INDEX, 0);

        setupHeader();
        setupClock();

        startTimeMillis = System.currentTimeMillis();
        timerHandler.post(sessionTimerRunnable);

        RecyclerView recyclerWorkout = findViewById(R.id.recyclerWorkoutExercises);
        recyclerWorkout.setLayoutManager(new LinearLayoutManager(this));
        workoutAdapter = new WorkoutExerciseAdapter(db);
        recyclerWorkout.setAdapter(workoutAdapter);

        // if started from a plan day, preload exercises
        if (planDayId != null) {
            preloadPlanExercises();
        }

        tvRestTimer = findViewById(R.id.tvRestTimerLabel);

        findViewById(R.id.btnRestTimer).setOnClickListener(v -> showRestTimerDialog());
        findViewById(R.id.btnFinishWorkout).setOnClickListener(v -> showFinishDialog());
        findViewById(R.id.btnAddExercise).setOnClickListener(v -> showExercisePicker());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void setupHeader() {
        TextView tvWorkoutTitle = findViewById(R.id.tvWorkoutTitle);
        tvClock = findViewById(R.id.tvWorkoutClock);

        tvClock.setText(new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date()));

        if (planId != null && planDayId != null) {
            AppExecutors.diskIO().execute(() -> {
                com.dropsett.app.model.WorkoutPlan plan =
                        db.workoutPlanDao().getPlanById(planId);
                com.dropsett.app.model.PlanDay day =
                        db.workoutPlanDao().getDayById(planDayId);
                runOnUiThread(() -> {
                    String title = (plan != null ? plan.name : "Workout")
                            + " — Day " + (planDayIndex + 1)
                            + (day != null && !day.label.isEmpty()
                            ? " (" + day.label + ")" : "");
                    tvWorkoutTitle.setText(title);
                });
            });
        } else {
            tvWorkoutTitle.setText("Today's Workout");
        }
    }

    private void setupClock() {
        clockHandler.post(clockRunnable);
    }

    private void preloadPlanExercises() {
        AppExecutors.diskIO().execute(() -> {
            List<com.dropsett.app.model.PlanExercise> planExercises =
                    db.workoutPlanDao().getExercisesForDay(planDayId);
            for (com.dropsett.app.model.PlanExercise pe : planExercises) {
                Exercise exercise = db.exerciseDao().getById(pe.exerciseId);
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
                        tvRestTimer.setText("Rest");
                    })
                    .setNegativeButton("Keep Running", null)
                    .show();
            return;
        }

        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_rest_timer, null);

        NumberPicker minutePicker = dialogView.findViewById(R.id.minutePicker);
        NumberPicker secondPicker = dialogView.findViewById(R.id.secondPicker);

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(10);
        minutePicker.setValue(2);

        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);
        secondPicker.setValue(0);

        new AlertDialog.Builder(this)
                .setTitle("Rest Timer")
                .setView(dialogView)
                .setPositiveButton("Start", (d, w) -> {
                    long total = (minutePicker.getValue() * 60L)
                            + secondPicker.getValue();
                    if (total > 0) startRestTimer(total);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void startRestTimer(long seconds) {
        tvRestTimer.setText(formatDuration(seconds));
        restTimerManager.start(seconds, new RestTimerManager.TimerListener() {
            @Override
            public void onTick(long secondsRemaining) {
                tvRestTimer.setText(formatDuration(secondsRemaining));
            }

            @Override
            public void onFinish() {
                tvRestTimer.setText("Rest");
                showTimerFinishedDialog();
            }
        });
    }

    private void showTimerFinishedDialog() {
        if (isFinishing() || isDestroyed()) return;
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(
                        500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
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
        restTimerManager.cancel();

        List<WorkoutExerciseAdapter.WorkoutExerciseItem> items =
                workoutAdapter.getExerciseItems();

        if (items.isEmpty()) {
            Toast.makeText(this, "No exercises to save", Toast.LENGTH_SHORT).show();
            return;
        }

        AppExecutors.diskIO().execute(() -> {
            WorkoutSession session = new WorkoutSession(
                    planId, planDayId, planDayIndex,
                    DateUtil.today(), elapsedSeconds, "");
            long sessionId = db.sessionDao().insertSession(session);

            for (int i = 0; i < items.size(); i++) {
                WorkoutExerciseAdapter.WorkoutExerciseItem item = items.get(i);

                // drop sets that are empty and have no history hint
                List<ExerciseSet> setsToSave = new java.util.ArrayList<>();
                for (ExerciseSet set : item.sets) {
                    boolean hasData = set.actualWeight > 0 || set.actualReps > 0;
                    boolean hasHint = set.hintWeight > 0 || set.hintReps > 0;
                    if (hasData) {
                        setsToSave.add(set);
                    } else if (hasHint) {
                        // promote hint to actual value
                        set.actualWeight = set.hintWeight;
                        set.actualReps = set.hintReps;
                        setsToSave.add(set);
                    }
                    // else: truly empty with no history — drop it
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