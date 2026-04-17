package com.dropsett.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.WorkoutSession;
import com.dropsett.app.util.AppExecutors;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        // footer nav
        findViewById(R.id.btnNavExercises).setOnClickListener(v ->
                startActivity(new Intent(this, ExerciseListActivity.class)));
        findViewById(R.id.btnNavPlans).setOnClickListener(v ->
                startActivity(new Intent(this, PlanListActivity.class)));
        findViewById(R.id.btnNavHistory).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        // workout start buttons
        findViewById(R.id.btnFreeWorkout).setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkoutActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnNextWorkout).setOnClickListener(v ->
                startNextPlanWorkout());

        findViewById(R.id.btnPickWorkout).setOnClickListener(v ->
                startActivity(new Intent(this, PlanPickerActivity.class)));
    }

    private void startNextPlanWorkout() {
        AppExecutors.diskIO().execute(() -> {
            WorkoutSession last = db.sessionDao().getLastSession();
            if (last == null || last.planId == null) {
                runOnUiThread(() ->
                        startActivity(new Intent(this, PlanPickerActivity.class)));
                return;
            }

            long planId = last.planId;
            java.util.List<com.dropsett.app.model.PlanDay> days =
                    db.workoutPlanDao().getDaysForPlan(planId);
            if (days.isEmpty()) {
                runOnUiThread(() ->
                        startActivity(new Intent(this, PlanPickerActivity.class)));
                return;
            }

            int lastDayIndex = last.planDayIndex;
            int nextDayIndex = (lastDayIndex + 1) % days.size();
            com.dropsett.app.model.PlanDay nextDay = days.get(nextDayIndex);

            runOnUiThread(() -> {
                Intent intent = new Intent(this, WorkoutActivity.class);
                intent.putExtra(WorkoutActivity.EXTRA_PLAN_ID, planId);
                intent.putExtra(WorkoutActivity.EXTRA_PLAN_DAY_ID, (long) nextDay.id);
                intent.putExtra(WorkoutActivity.EXTRA_PLAN_DAY_INDEX, nextDayIndex);
                startActivity(intent);
            });
        });
    }
}