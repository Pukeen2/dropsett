package com.dropsett.app.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "plan_exercises",
        foreignKeys = {
                @ForeignKey(
                        entity = PlanDay.class,
                        parentColumns = "id",
                        childColumns = "planDayId",
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
public class PlanExercise {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long planDayId;
    public long exerciseId;
    public int targetSets;
    public int targetRpe;      // suggested RPE 1-10, 0 if not set
    public int sortOrder;

    public PlanExercise(long planDayId, long exerciseId,
                        int targetSets, int targetRpe, int sortOrder) {
        this.planDayId = planDayId;
        this.exerciseId = exerciseId;
        this.targetSets = targetSets;
        this.targetRpe = targetRpe;
        this.sortOrder = sortOrder;
    }
}