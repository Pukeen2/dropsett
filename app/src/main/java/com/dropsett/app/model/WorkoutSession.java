package com.dropsett.app.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "workout_sessions",
        foreignKeys = @ForeignKey(
                entity = WorkoutPlan.class,
                parentColumns = "id",
                childColumns = "planId",
                onDelete = ForeignKey.SET_NULL
        )
)
public class WorkoutSession {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public Long planId;
    public Long planDayId;
    public int planDayIndex;
    public String date;
    public long durationSeconds;
    public String notes;

    public WorkoutSession(Long planId, Long planDayId, int planDayIndex,
                          String date, long durationSeconds, String notes) {
        this.planId        = planId;
        this.planDayId     = planDayId;
        this.planDayIndex  = planDayIndex;
        this.date          = date;
        this.durationSeconds = durationSeconds;
        this.notes         = notes;
    }
}