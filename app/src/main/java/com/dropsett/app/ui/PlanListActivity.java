package com.dropsett.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

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

        adapter.setOnPlanDeleteListener(plan -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Delete Plan")
                    .setMessage("Delete \"" + plan.name + "\"? This cannot be undone.")
                    .setPositiveButton("Delete", (d, w) -> {
                        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() ->
                                db.workoutPlanDao().deletePlan(plan));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        TextView tvEmpty = findViewById(R.id.tvEmptyPlans);
        db.workoutPlanDao().getAllPlans().observe(this, plans -> {
            adapter.setPlans(plans);
            com.dropsett.app.util.EmptyStateHelper.observe(recyclerView, tvEmpty, plans.size());
        });

        FloatingActionButton fab = findViewById(R.id.fabAddPlan);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, PlanBuilderActivity.class)));

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