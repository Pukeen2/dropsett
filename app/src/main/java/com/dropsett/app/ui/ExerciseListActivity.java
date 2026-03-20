package com.dropsett.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.Exercise;
import com.dropsett.app.ui.adapter.ExerciseAdapter;
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
        recyclerView.setAdapter(adapter);

        db.exerciseDao().getAllExercises().observe(this, exercises -> {
            adapter.setExercises(exercises);
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
                        Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
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