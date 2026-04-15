package com.dropsett.app.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.dropsett.app.model.PlanDay;
import com.dropsett.app.model.PlanExercise;
import com.dropsett.app.model.WorkoutPlan;

import java.util.List;

@Dao
public interface WorkoutPlanDao {

    @Insert
    long insertPlan(WorkoutPlan plan);

    @Delete
    void deletePlan(WorkoutPlan plan);

    @Query("SELECT * FROM workout_plans ORDER BY createdAt DESC")
    LiveData<List<WorkoutPlan>> getAllPlans();

    @Query("SELECT * FROM workout_plans ORDER BY createdAt DESC")
    List<WorkoutPlan> getAllPlansSync();

    @Query("SELECT * FROM workout_plans WHERE id = :id")
    WorkoutPlan getPlanById(long id);

    @Insert
    long insertDay(PlanDay day);

    @Query("SELECT * FROM plan_days WHERE planId = :planId ORDER BY dayIndex ASC")
    List<PlanDay> getDaysForPlan(long planId);

    @Query("SELECT * FROM plan_days WHERE id = :dayId")
    PlanDay getDayById(long dayId);

    @Insert
    long insertPlanExercise(PlanExercise planExercise);

    @Delete
    void deletePlanExercise(PlanExercise planExercise);

    @Query("SELECT * FROM plan_exercises WHERE planDayId = :dayId ORDER BY sortOrder ASC")
    List<PlanExercise> getExercisesForDay(long dayId);
}