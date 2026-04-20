package com.dropsett.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.PlanDay;
import com.dropsett.app.model.WorkoutPlan;
import com.dropsett.app.model.WorkoutSession;
import com.dropsett.app.util.AppExecutors;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private TextView tvNextWorkoutSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
        tvNextWorkoutSub = findViewById(R.id.tvNextWorkoutSub);

        findViewById(R.id.btnNavExercises).setOnClickListener(v ->
                startActivity(new Intent(this, ExerciseListActivity.class)));
        findViewById(R.id.btnNavPlans).setOnClickListener(v ->
                startActivity(new Intent(this, PlanListActivity.class)));
        findViewById(R.id.btnNavHistory).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        findViewById(R.id.btnFreeWorkout).setOnClickListener(v ->
                startActivity(new Intent(this, WorkoutActivity.class)));

        findViewById(R.id.btnNextWorkout).setOnClickListener(v ->
                startNextPlanWorkout());

        findViewById(R.id.btnPickWorkout).setOnClickListener(v ->
                startActivity(new Intent(this, PlanPickerActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNextWorkoutPreview();
    }

    private void loadNextWorkoutPreview() {
        AppExecutors.diskIO().execute(() -> {
            WorkoutSession last = db.sessionDao().getLastSession();
            if (last == null || last.planId == null) {
                runOnUiThread(() ->
                        tvNextWorkoutSub.setText("No plan started yet"));
                return;
            }

            WorkoutPlan plan = db.workoutPlanDao().getPlanById(last.planId);
            List<PlanDay> days = db.workoutPlanDao().getDaysForPlan(last.planId);
            if (days.isEmpty() || plan == null) {
                runOnUiThread(() ->
                        tvNextWorkoutSub.setText("No plan found"));
                return;
            }

            int nextDayIndex = (last.planDayIndex + 1) % days.size();
            PlanDay nextDay = null;
            for (PlanDay d : days) {
                if (d.dayIndex == nextDayIndex) {
                    nextDay = d;
                    break;
                }
            }
            if (nextDay == null) nextDay = days.get(0);

            final String subtitle = plan.name
                    + " — Day " + (nextDayIndex + 1)
                    + (nextDay.label != null && !nextDay.label.isEmpty()
                    ? " (" + nextDay.label + ")" : "");

            runOnUiThread(() -> tvNextWorkoutSub.setText(subtitle));
        });
    }

    private void startNextPlanWorkout() {
        AppExecutors.diskIO().execute(() -> {
            WorkoutSession last = db.sessionDao().getLastSession();
            if (last == null || last.planId == null) {
                runOnUiThread(() ->
                        startActivity(new Intent(this, PlanPickerActivity.class)));
                return;
            }

            List<PlanDay> days = db.workoutPlanDao().getDaysForPlan(last.planId);
            if (days.isEmpty()) {
                runOnUiThread(() ->
                        startActivity(new Intent(this, PlanPickerActivity.class)));
                return;
            }

            int nextDayIndex = (last.planDayIndex + 1) % days.size();
            PlanDay nextDay = null;
            for (PlanDay d : days) {
                if (d.dayIndex == nextDayIndex) {
                    nextDay = d;
                    break;
                }
            }
            if (nextDay == null) nextDay = days.get(0);

            final PlanDay finalNextDay = nextDay;
            final int finalNextIndex = nextDayIndex;

            runOnUiThread(() -> {
                Intent intent = new Intent(this, WorkoutActivity.class);
                intent.putExtra(WorkoutActivity.EXTRA_PLAN_ID, last.planId);
                intent.putExtra(WorkoutActivity.EXTRA_PLAN_DAY_ID, finalNextDay.id);
                intent.putExtra(WorkoutActivity.EXTRA_PLAN_DAY_INDEX, finalNextIndex);
                startActivity(intent);
            });
        });
    }
}