package com.dropsett.app.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.PlanDay;
import com.dropsett.app.model.WorkoutPlan;
import com.dropsett.app.ui.adapter.PlanDayBuilderAdapter;
import com.dropsett.app.util.DateUtil;

import java.util.concurrent.Executors;

public class PlanBuilderActivity extends AppCompatActivity {

    private AppDatabase db;
    private PlanDayBuilderAdapter dayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_builder);

        db = AppDatabase.getInstance(this);

        EditText etPlanName = findViewById(R.id.etPlanName);
        RecyclerView recyclerDays = findViewById(R.id.recyclerDays);
        Button btnSave = findViewById(R.id.btnSavePlan);

        recyclerDays.setLayoutManager(new LinearLayoutManager(this));
        dayAdapter = new PlanDayBuilderAdapter();
        recyclerDays.setAdapter(dayAdapter);

        btnSave.setOnClickListener(v -> {
            String name = etPlanName.getText().toString().trim();
            if (name.isEmpty()) {
                etPlanName.setError("Plan name is required");
                etPlanName.requestFocus();
                return;
            }

            btnSave.setEnabled(false);
            btnSave.setText("Saving...");

            Executors.newSingleThreadExecutor().execute(() -> {
                WorkoutPlan plan = new WorkoutPlan(name, DateUtil.today());
                long planId = db.workoutPlanDao().insertPlan(plan);

                String[] labels = dayAdapter.getDayLabels();
                for (int i = 0; i < 7; i++) {
                    PlanDay day = new PlanDay(planId, i, labels[i]);
                    db.workoutPlanDao().insertDay(day);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Plan saved!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("New Plan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}