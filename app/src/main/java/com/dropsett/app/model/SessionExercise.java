package com.dropsett.app.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "session_exercises",
        foreignKeys = {
                @ForeignKey(
                        entity = WorkoutSession.class,
                        parentColumns = "id",
                        childColumns = "sessionId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Exercise.class,
                        parentColumns = "id",
                        childColumns = "exerciseId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class SessionExercise {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long sessionId;
    public long exerciseId;
    public int sortOrder;

    public SessionExercise(long sessionId, long exerciseId, int sortOrder) {
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.sortOrder = sortOrder;
    }
}