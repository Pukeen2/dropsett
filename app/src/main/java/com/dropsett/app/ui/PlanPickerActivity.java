package com.dropsett.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.PlanDay;
import com.dropsett.app.model.WorkoutPlan;
import com.dropsett.app.ui.adapter.PlanAdapter;
import com.dropsett.app.ui.adapter.PlanDayAdapter;
import com.dropsett.app.util.AppExecutors;

import java.util.List;

public class PlanPickerActivity extends AppCompatActivity {

    private AppDatabase db;
    private RecyclerView recyclerDays;
    private PlanDayAdapter dayAdapter;
    private long selectedPlanId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_picker);

        db = AppDatabase.getInstance(this);

        RecyclerView recyclerPlans = findViewById(R.id.recyclerPickPlan);
        recyclerPlans.setLayoutManager(new LinearLayoutManager(this));

        recyclerDays = findViewById(R.id.recyclerPickDay);
        recyclerDays.setLayoutManager(new LinearLayoutManager(this));

        PlanAdapter planAdapter = new PlanAdapter(plan -> {
            selectedPlanId = plan.id;
            loadDaysForPlan(plan.id);
        });
        recyclerPlans.setAdapter(planAdapter);

        db.workoutPlanDao().getAllPlans().observe(this, planAdapter::setPlans);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Choose Workout");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadDaysForPlan(long planId) {
        AppExecutors.diskIO().execute(() -> {
            List<PlanDay> days = db.workoutPlanDao().getDaysForPlan(planId);
            runOnUiThread(() -> {
                dayAdapter = new PlanDayAdapter(days, day -> {
                    Intent intent = new Intent(this, WorkoutActivity.class);
                    intent.putExtra(WorkoutActivity.EXTRA_PLAN_ID, selectedPlanId);
                    intent.putExtra(WorkoutActivity.EXTRA_PLAN_DAY_ID, day.id);
                    intent.putExtra(WorkoutActivity.EXTRA_PLAN_DAY_INDEX, day.dayIndex);
                    startActivity(intent);
                });
                recyclerDays.setAdapter(dayAdapter);
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}