package com.dropsett.app.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercises")
public class Exercise {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String muscleGroup;       // primary muscle group
    public String secondaryMuscle;   // secondary muscle group, may be empty
    public String movementPattern;   // Push / Pull / Hinge / Squat / Carry / Core / Other
    public String equipmentType;     // Barbell / Dumbbell / Cable / Machine / Bodyweight / Band / Kettlebell
    public boolean isCompound;       // true = compound, false = isolation
    public String notes;

    public Exercise(String name, String muscleGroup, String secondaryMuscle,
                    String movementPattern, String equipmentType,
                    boolean isCompound, String notes) {
        this.name             = name;
        this.muscleGroup      = muscleGroup;
        this.secondaryMuscle  = secondaryMuscle;
        this.movementPattern  = movementPattern;
        this.equipmentType    = equipmentType;
        this.isCompound       = isCompound;
        this.notes            = notes;
    }
}