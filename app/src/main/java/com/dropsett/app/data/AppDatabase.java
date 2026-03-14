package com.dropsett.app.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.dropsett.app.model.Exercise;
import com.dropsett.app.model.ExerciseSet;
import com.dropsett.app.model.PlanDay;
import com.dropsett.app.model.PlanExercise;
import com.dropsett.app.model.SessionExercise;
import com.dropsett.app.model.WorkoutPlan;
import com.dropsett.app.model.WorkoutSession;

@Database(
        entities = {
                Exercise.class,
                WorkoutPlan.class,
                PlanDay.class,
                PlanExercise.class,
                WorkoutSession.class,
                SessionExercise.class,
                ExerciseSet.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract ExerciseDao exerciseDao();
    public abstract WorkoutPlanDao workoutPlanDao();
    public abstract SessionDao sessionDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "dropsett.db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}