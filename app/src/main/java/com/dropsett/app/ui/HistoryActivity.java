package com.dropsett.app.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.PlanDay;
import com.dropsett.app.model.WorkoutPlan;
import com.dropsett.app.model.WorkoutSession;
import com.dropsett.app.ui.adapter.SessionHistoryAdapter;
import com.dropsett.app.util.AppExecutors;
import com.dropsett.app.util.DateUtil;
import com.dropsett.app.util.EmptyStateHelper;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        AppDatabase db = AppDatabase.getInstance(this);
        RecyclerView recycler = findViewById(R.id.recyclerHistory);
        TextView tvEmpty = findViewById(R.id.tvEmptyHistory);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));

        SessionHistoryAdapter adapter = new SessionHistoryAdapter(session ->
                SessionDetailActivity.start(this, session.id));
        recycler.setAdapter(adapter);

        adapter.setOnSessionDeleteListener(session -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Delete Workout")
                    .setMessage("Delete this workout from "
                            + DateUtil.formatDisplay(session.date) + "?")
                    .setPositiveButton("Delete", (d, w) ->
                            AppExecutors.diskIO().execute(() ->
                                    db.sessionDao().deleteSession(session.id)))
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        db.sessionDao().getAllSessions().observe(this, sessions -> {
            EmptyStateHelper.observe(recycler, tvEmpty, sessions.size());
            AppExecutors.diskIO().execute(() -> {
                List<SessionHistoryAdapter.SessionItem> items = new ArrayList<>();
                for (WorkoutSession session : sessions) {
                    String label;
                    if (session.planId != null) {
                        WorkoutPlan plan = db.workoutPlanDao()
                                .getPlanById(session.planId);
                        List<PlanDay> days = db.workoutPlanDao()
                                .getDaysForPlan(session.planId);
                        String planName = plan != null ? plan.name : "Unknown Plan";
                        String dayLabel = "";
                        for (PlanDay d : days) {
                            if (d.dayIndex == session.planDayIndex) {
                                dayLabel = d.label != null && !d.label.isEmpty()
                                        ? " (" + d.label + ")" : "";
                                break;
                            }
                        }
                        label = planName + " — Day "
                                + (session.planDayIndex + 1) + dayLabel;
                    } else {
                        label = "Free Workout";
                    }
                    items.add(new SessionHistoryAdapter.SessionItem(session, label));
                }
                runOnUiThread(() -> adapter.setItems(items));
            });
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Workout History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}