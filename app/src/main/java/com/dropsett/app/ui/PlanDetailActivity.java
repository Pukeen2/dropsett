package com.dropsett.app.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.Exercise;
import com.dropsett.app.model.PlanDay;
import com.dropsett.app.model.PlanExercise;
import com.dropsett.app.model.WorkoutPlan;
import com.dropsett.app.ui.adapter.PlanDetailAdapter;
import com.dropsett.app.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class PlanDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);

        long planId = getIntent().getLongExtra("planId", -1);
        AppDatabase db = AppDatabase.getInstance(this);

        TextView tvPlanName = findViewById(R.id.tvPlanName);
        RecyclerView recycler = findViewById(R.id.recyclerPlanDays);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        AppExecutors.diskIO().execute(() -> {
            WorkoutPlan plan = db.workoutPlanDao().getPlanById(planId);
            List<PlanDay> days = db.workoutPlanDao().getDaysForPlan(planId);

            List<PlanDetailAdapter.DayDetailItem> items = new ArrayList<>();
            for (PlanDay day : days) {
                List<PlanExercise> planExercises =
                        db.workoutPlanDao().getExercisesForDay(day.id);
                List<Exercise> exercises = new ArrayList<>();
                for (PlanExercise pe : planExercises) {
                    exercises.add(db.exerciseDao().getById(pe.exerciseId));
                }
                items.add(new PlanDetailAdapter.DayDetailItem(day, planExercises, exercises));
            }

            runOnUiThread(() -> {
                tvPlanName.setText(plan.name);
                PlanDetailAdapter adapter = new PlanDetailAdapter(items);
                recycler.setAdapter(adapter);
            });
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Plan Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}