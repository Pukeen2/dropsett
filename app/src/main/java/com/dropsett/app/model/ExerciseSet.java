package com.dropsett.app.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "exercise_sets",
        foreignKeys = @ForeignKey(
                entity = SessionExercise.class,
                parentColumns = "id",
                childColumns = "sessionExerciseId",
                onDelete = ForeignKey.CASCADE
        )
)
public class ExerciseSet {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long sessionExerciseId;
    public int setIndex;
    public int targetReps;
    public float targetWeight;
    public int actualReps;
    public float actualWeight;
    public int rpe;
    public boolean toFailure;

    // not stored in DB — used at runtime for hint display and hint-to-actual promotion
    @Ignore public float hintWeight;
    @Ignore public int hintReps;

    public ExerciseSet(long sessionExerciseId, int setIndex,
                       int targetReps, float targetWeight,
                       int actualReps, float actualWeight,
                       int rpe, boolean toFailure) {
        this.sessionExerciseId = sessionExerciseId;
        this.setIndex = setIndex;
        this.targetReps = targetReps;
        this.targetWeight = targetWeight;
        this.actualReps = actualReps;
        this.actualWeight = actualWeight;
        this.rpe = rpe;
        this.toFailure = toFailure;
    }
}