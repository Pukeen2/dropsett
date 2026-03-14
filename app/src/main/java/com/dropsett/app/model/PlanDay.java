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

    public long planId;         // which plan this day belongs to
    public int dayOfWeek;       // 0 = Monday ... 6 = Sunday
    public String label;        // e.g. "Push Day", "Rest"

    public PlanDay(long planId, int dayOfWeek, String label) {
        this.planId = planId;
        this.dayOfWeek = dayOfWeek;
        this.label = label;
    }
}