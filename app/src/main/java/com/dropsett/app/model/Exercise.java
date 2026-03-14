package com.dropsett.app.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercises")
public class Exercise {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String muscleGroup;  // e.g. "Chest", "Back", "Legs"
    public String notes;        // optional user notes about the exercise

    public Exercise(String name, String muscleGroup, String notes) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.notes = notes;
    }
}