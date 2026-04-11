package com.dropsett.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.Exercise;
import com.dropsett.app.ui.adapter.ExerciseAdapter;
import com.dropsett.app.util.EmptyStateHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.Executors;

public class ExerciseListActivity extends AppCompatActivity {

    private AppDatabase db;
    private ExerciseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        db = AppDatabase.getInstance(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerExercises);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseAdapter();
        adapter.setOnExerciseClickListener(exercise -> {
            ExerciseHistoryActivity.start(this, exercise.id);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(
                new androidx.recyclerview.widget.DividerItemDecoration(
                        this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                )
        );

        TextView tvEmpty = findViewById(R.id.tvEmptyExercises);

        db.exerciseDao().getAllExercises().observe(this, exercises -> {
            adapter.setExercises(exercises);
            EmptyStateHelper.observe(recyclerView, tvEmpty, exercises.size());
        });


        FloatingActionButton fab = findViewById(R.id.fabAddExercise);
        fab.setOnClickListener(v -> showAddExerciseDialog());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Exercises");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showAddExerciseDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_exercise, null);

        EditText etName = dialogView.findViewById(R.id.etExerciseName);
        EditText etMuscle = dialogView.findViewById(R.id.etMuscleGroup);
        EditText etNotes = dialogView.findViewById(R.id.etNotes);

        new AlertDialog.Builder(this)
                .setTitle("Add Exercise")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String muscle = etMuscle.getText().toString().trim();
                    String notes = etNotes.getText().toString().trim();

                    if (name.isEmpty()) {
                        etName.setError("Name is required");
                        return;
                    }

                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.exerciseDao().insert(new Exercise(name, muscle, notes));
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}