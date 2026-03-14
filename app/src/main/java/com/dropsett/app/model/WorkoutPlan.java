package com.dropsett.app.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_plans")
public class WorkoutPlan {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;         // e.g. "My Push Pull Legs"
    public String createdAt;    // ISO date string

    public WorkoutPlan(String name, String createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }
}