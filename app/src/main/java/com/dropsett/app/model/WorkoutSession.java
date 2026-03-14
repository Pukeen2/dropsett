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

    public Long planId;         // nullable — null if freeform session
    public Integer planDayId;   // nullable — which plan day this was based on
    public String date;         // ISO date string e.g. "2026-03-27"
    public long durationSeconds;
    public String notes;

    public WorkoutSession(Long planId, Integer planDayId,
                          String date, long durationSeconds, String notes) {
        this.planId = planId;
        this.planDayId = planDayId;
        this.date = date;
        this.durationSeconds = durationSeconds;
        this.notes = notes;
    }
}