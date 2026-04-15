package com.dropsett.app.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "plan_days",
        foreignKeys = @ForeignKey(
                entity = WorkoutPlan.class,
                parentColumns = "id",
                childColumns = "planId",
                onDelete = ForeignKey.CASCADE
        )
)
public class PlanDay {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long planId;
    public int dayIndex;   // 0-based order within the plan, no connection to day of week
    public String label;   // e.g. "Push", "Pull", "Legs"

    public PlanDay(long planId, int dayIndex, String label) {
        this.planId = planId;
        this.dayIndex = dayIndex;
        this.label = label;
    }
}