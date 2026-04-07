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
import com.dropsett.app.ui.adapter.ExerciseHistoryAdapter;
import com.dropsett.app.util.AppExecutors;

import java.util.List;

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

        TextView tvExerciseName = findViewById(R.id.tvExerciseHistoryName);
        RecyclerView recycler = findViewById(R.id.recyclerExerciseHistory);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        AppExecutors.diskIO().execute(() -> {
            Exercise exercise = db.exerciseDao().getById(exerciseId);
            List<ExerciseSet> sets = db.sessionDao().getAllSetsForExercise(exerciseId);

            runOnUiThread(() -> {
                tvExerciseName.setText(exercise.name);
                ExerciseHistoryAdapter adapter = new ExerciseHistoryAdapter(sets);
                recycler.setAdapter(adapter);
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