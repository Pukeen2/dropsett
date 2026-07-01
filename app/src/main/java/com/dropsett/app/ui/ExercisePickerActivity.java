package com.dropsett.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.Exercise;
import com.dropsett.app.ui.adapter.ExercisePickerAdapter;
import com.dropsett.app.util.AppExecutors;
import com.dropsett.app.util.UserPreferences;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class ExercisePickerActivity extends AppCompatActivity {

    public static final String EXTRA_EXERCISE_ID   = "picked_exercise_id";
    public static final String EXTRA_EXERCISE_NAME = "picked_exercise_name";

    private AppDatabase db;
    private UserPreferences prefs;
    private ExercisePickerAdapter adapter;

    private List<Exercise> allExercises   = new ArrayList<>();
    private List<Exercise> recentExercises = new ArrayList<>();
    private String currentMuscleFilter    = null;
    private String currentSearchQuery     = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_picker);

        db    = AppDatabase.getInstance(this);
        prefs = new UserPreferences(this);

        EditText etSearch      = findViewById(R.id.etExerciseSearch);
        ChipGroup chipGroup    = findViewById(R.id.chipGroupMuscle);
        RecyclerView recycler  = findViewById(R.id.recyclerPickerExercises);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExercisePickerAdapter(exercise -> {
            Intent result = new Intent();
            result.putExtra(EXTRA_EXERCISE_ID,   exercise.id);
            result.putExtra(EXTRA_EXERCISE_NAME, exercise.name);
            setResult(Activity.RESULT_OK, result);
            finish();
        });
        recycler.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = s.toString().trim().toLowerCase();
                applyFilters();
            }
        });

        loadData(chipGroup);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Choose Exercise");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadData(ChipGroup chipGroup) {
        List<String> allowed = prefs.getAllowedEquipmentTypes();

        AppExecutors.diskIO().execute(() -> {
            List<Exercise> exercises = db.exerciseDao()
                    .getExercisesByEquipmentSync(allowed);
            List<Long> recentIds = db.exerciseDao()
                    .getRecentlyUsedExerciseIds();
            List<Exercise> recent = new ArrayList<>();
            for (Long id : recentIds) {
                for (Exercise e : exercises) {
                    if (e.id == id) {
                        recent.add(e);
                        break;
                    }
                }
            }
            List<String> muscleGroups = db.exerciseDao().getAllMuscleGroups();

            runOnUiThread(() -> {
                allExercises    = exercises;
                recentExercises = recent;

                buildMuscleChips(chipGroup, muscleGroups);
                applyFilters();
            });
        });
    }

    private void buildMuscleChips(ChipGroup chipGroup, List<String> muscleGroups) {
        chipGroup.removeAllViews();

        Chip allChip = new Chip(this);
        allChip.setText("All");
        allChip.setCheckable(true);
        allChip.setChecked(true);
        allChip.setOnClickListener(v -> {
            currentMuscleFilter = null;
            applyFilters();
        });
        chipGroup.addView(allChip);

        for (String muscle : muscleGroups) {
            Chip chip = new Chip(this);
            chip.setText(muscle);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                currentMuscleFilter = muscle;
                allChip.setChecked(false);
                applyFilters();
            });
            chipGroup.addView(chip);
        }
    }

    private void applyFilters() {
        List<Exercise> filtered = new ArrayList<>();
        for (Exercise e : allExercises) {
            boolean matchesMuscle = currentMuscleFilter == null
                    || e.muscleGroup.equals(currentMuscleFilter);
            boolean matchesSearch = currentSearchQuery.isEmpty()
                    || e.name.toLowerCase().contains(currentSearchQuery);
            if (matchesMuscle && matchesSearch) {
                filtered.add(e);
            }
        }

        // recently used section — only show when no search or filter active
        List<Exercise> recentFiltered = new ArrayList<>();
        if (currentSearchQuery.isEmpty() && currentMuscleFilter == null) {
            recentFiltered.addAll(recentExercises);
        }

        adapter.submitData(recentFiltered, filtered);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}