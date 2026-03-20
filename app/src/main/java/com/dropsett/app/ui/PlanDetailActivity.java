package com.dropsett.app.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.PlanDay;
import com.dropsett.app.model.WorkoutPlan;
import com.dropsett.app.ui.adapter.PlanDayAdapter;
import com.dropsett.app.util.DateUtil;

import java.util.List;
import java.util.concurrent.Executors;

public class PlanDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);

        long planId = getIntent().getLongExtra("planId", -1);
        AppDatabase db = AppDatabase.getInstance(this);

        TextView tvPlanName = findViewById(R.id.tvPlanName);
        RecyclerView recyclerDays = findViewById(R.id.recyclerPlanDays);
        recyclerDays.setLayoutManager(new LinearLayoutManager(this));

        Executors.newSingleThreadExecutor().execute(() -> {
            WorkoutPlan plan = db.workoutPlanDao().getPlanById(planId);
            List<PlanDay> days = db.workoutPlanDao().getDaysForPlan(planId);

            runOnUiThread(() -> {
                tvPlanName.setText(plan.name);
                PlanDayAdapter adapter = new PlanDayAdapter(days);
                recyclerDays.setAdapter(adapter);
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