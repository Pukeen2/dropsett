package com.dropsett.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.Exercise;
import com.dropsett.app.model.ExerciseSet;
import com.dropsett.app.model.SessionExercise;
import com.dropsett.app.model.WorkoutSession;
import com.dropsett.app.ui.adapter.ExerciseHistoryAdapter;
import com.dropsett.app.util.AppExecutors;
import com.dropsett.app.util.DateUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExerciseHistoryActivity extends AppCompatActivity {

    private static final String EXTRA_EXERCISE_ID = "exerciseId";

    public static void start(Context context, long exerciseId) {
        Intent intent = new Intent(context, ExerciseHistoryActivity.class);
        intent.putExtra(EXTRA_EXERCISE_ID, exerciseId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_history);

        long exerciseId = getIntent().getLongExtra(EXTRA_EXERCISE_ID, -1);
        AppDatabase db = AppDatabase.getInstance(this);

        TextView tvName = findViewById(R.id.tvExerciseHistoryName);
        RecyclerView recycler = findViewById(R.id.recyclerExerciseHistory);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        AppExecutors.diskIO().execute(() -> {
            Exercise exercise = db.exerciseDao().getById(exerciseId);
            List<ExerciseSet> allSets = db.sessionDao().getAllSetsForExercise(exerciseId);

            // group sets by sessionExerciseId, preserving order (most recent first)
            Map<Long, List<ExerciseSet>> grouped = new LinkedHashMap<>();
            for (ExerciseSet set : allSets) {
                grouped.computeIfAbsent(set.sessionExerciseId, k -> new ArrayList<>()).add(set);
            }

            List<ExerciseHistoryAdapter.SetGroup> groups = new ArrayList<>();
            for (Map.Entry<Long, List<ExerciseSet>> entry : grouped.entrySet()) {
                long seId = entry.getKey();
                String sessionLabel = "Unknown date";
                WorkoutSession ws = db.sessionDao().getSessionByExerciseSetId(seId);
                if (ws != null) {
                    sessionLabel = DateUtil.formatDisplay(ws.date);
                }
                groups.add(new ExerciseHistoryAdapter.SetGroup(
                        seId, sessionLabel, entry.getValue()));
            }

            final String name = exercise != null ? exercise.name : "Exercise";
            final List<ExerciseHistoryAdapter.SetGroup> finalGroups = groups;
            runOnUiThread(() -> {
                tvName.setText(name);
                recycler.setAdapter(new ExerciseHistoryAdapter(finalGroups));
            });
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Exercise History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}