package com.dropsett.app.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.ui.adapter.PlanAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PlanListActivity extends AppCompatActivity {

    private AppDatabase db;
    private PlanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);

        db = AppDatabase.getInstance(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerPlans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlanAdapter(plan -> {
            Intent intent = new Intent(this, PlanDetailActivity.class);
            intent.putExtra("planId", plan.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        db.workoutPlanDao().getAllPlans().observe(this, plans -> {
            adapter.setPlans(plans);
        });

        FloatingActionButton fab = findViewById(R.id.fabAddPlan);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlanBuilderActivity.class);
            startActivity(intent);
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Workout Plans");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}