package com.dropsett.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dropsett.app.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStartWorkout = findViewById(R.id.btnStartWorkout);
        Button btnExercises = findViewById(R.id.btnExercises);
        Button btnPlans = findViewById(R.id.btnPlans);

        btnStartWorkout.setOnClickListener(v ->
                startActivity(new Intent(this, WorkoutActivity.class)));

        btnExercises.setOnClickListener(v ->
                startActivity(new Intent(this, ExerciseListActivity.class)));

        btnPlans.setOnClickListener(v ->
                startActivity(new Intent(this, PlanListActivity.class)));
    }
}