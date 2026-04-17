package com.dropsett.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;

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

import java.util.List;

public class WorkoutActivity extends AppCompatActivity {

    public static final String EXTRA_PLAN_ID = "planId";
    public static final String EXTRA_PLAN_DAY_ID = "planDayId";
    public static final String EXTRA_PLAN_DAY_INDEX = "planDayIndex";

    private AppDatabase db;
    private WorkoutExerciseAdapter workoutAdapter;
    private RestTimerManager restTimerManager;

    private Long planId = null;
    private Integer planDayId = null;
    private long startTimeMillis;
    private long elapsedSeconds = 0;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private TextView tvTimer;
    private Button btnRestTimer;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedSeconds = (System.currentTimeMillis() - startTimeMillis) / 1000;
            tvTimer.setText(formatDuration(elapsedSeconds));
            timerHandler.postDelayed(this, 1000);
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

        tvTimer = findViewById(R.id.tvSessionTimer);
        startTimeMillis = System.currentTimeMillis();
        timerHandler.post(timerRunnable);

        RecyclerView recyclerWorkout = findViewById(R.id.recyclerWorkoutExercises);
        recyclerWorkout.setLayoutManager(new LinearLayoutManager(this));
        workoutAdapter = new WorkoutExerciseAdapter(db);
        recyclerWorkout.setAdapter(workoutAdapter);

        Button btnAddExercise = findViewById(R.id.btnAddExercise);
        btnAddExercise.setOnClickListener(v -> showExercisePicker());

        Button btnFinish = findViewById(R.id.btnFinishWorkout);
        btnFinish.setOnClickListener(v -> showFinishDialog());

        btnRestTimer = findViewById(R.id.btnRestTimer);
        btnRestTimer.setOnClickListener(v -> showRestTimerDialog());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Workout");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    private void showRestTimerDialog() {
        // if timer already running, show cancel option instead
        if (restTimerManager.isRunning()) {
            new AlertDialog.Builder(this)
                    .setTitle("Rest Timer Running")
                    .setMessage("Cancel the current rest timer?")
                    .setPositiveButton("Cancel Timer", (d, w) -> {
                        restTimerManager.cancel();
                        btnRestTimer.setText("Rest Timer");
                    })
                    .setNegativeButton("Keep Running", null)
                    .show();
            return;
        }

        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_rest_timer, null);

        NumberPicker minutePicker = dialogView.findViewById(R.id.minutePicker);
        NumberPicker secondPicker = dialogView.findViewById(R.id.secondPicker);
        TextView tvCountdown = dialogView.findViewById(R.id.tvCountdown);

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(10);
        minutePicker.setValue(2);

        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);
        secondPicker.setValue(0);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Rest Timer")
                .setView(dialogView)
                .setPositiveButton("Start", (d, w) -> {
                    long totalSeconds = (minutePicker.getValue() * 60L)
                            + secondPicker.getValue();
                    if (totalSeconds == 0) return;
                    startRestTimer(totalSeconds);
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void startRestTimer(long seconds) {
        btnRestTimer.setText(formatDuration(seconds));

        restTimerManager.start(seconds, new RestTimerManager.TimerListener() {
            @Override
            public void onTick(long secondsRemaining) {
                btnRestTimer.setText(formatDuration(secondsRemaining));
            }

            @Override
            public void onFinish() {
                btnRestTimer.setText("Rest Timer");
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
            workoutAdapter.addExercise(exercise);
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
        timerHandler.removeCallbacks(timerRunnable);
        restTimerManager.cancel();

        List<WorkoutExerciseAdapter.WorkoutExerciseItem> items =
                workoutAdapter.getExerciseItems();

        if (items.isEmpty()) {
            Toast.makeText(this, "No exercises to save", Toast.LENGTH_SHORT).show();
            return;
        }

        AppExecutors.diskIO().execute(() -> {
            int dayIndex = getIntent().getIntExtra(EXTRA_PLAN_DAY_INDEX, 0);

            WorkoutSession session = new WorkoutSession(
                    planId, planDayId, dayIndex,
                    DateUtil.today(),
                    elapsedSeconds,
                    ""
            );
            long sessionId = db.sessionDao().insertSession(session);

            for (int i = 0; i < items.size(); i++) {
                WorkoutExerciseAdapter.WorkoutExerciseItem item = items.get(i);
                SessionExercise se = new SessionExercise(
                        sessionId, item.exercise.id, i);
                long seId = db.sessionDao().insertSessionExercise(se);

                for (ExerciseSet set : item.sets) {
                    set.sessionExerciseId = seId;
                    db.sessionDao().insertSet(set);
                }
            }

            runOnUiThread(() -> {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        "Workout saved!",
                        Snackbar.LENGTH_SHORT
                ).show();
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
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
        restTimerManager.cancel();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leave Workout?")
                .setMessage("Your workout will not be saved.")
                .setPositiveButton("Leave", (d, w) -> {
                    timerHandler.removeCallbacks(timerRunnable);
                    restTimerManager.cancel();
                    finish();
                })
                .setNegativeButton("Stay", null)
                .show();
    }
}