package com.dropsett.app.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dropsett.app.model.Exercise;
import com.dropsett.app.model.ExerciseSet;
import com.dropsett.app.model.PlanDay;
import com.dropsett.app.model.PlanExercise;
import com.dropsett.app.model.SessionExercise;
import com.dropsett.app.model.WorkoutPlan;
import com.dropsett.app.model.WorkoutSession;

import java.util.concurrent.Executors;

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
        version = 2,
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
                            )
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadExecutor().execute(() ->
                                            seedExercises(AppDatabase.getInstance(context)));
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void seedExercises(AppDatabase db) {
        ExerciseDao dao = db.exerciseDao();

        // The big 5
        dao.insert(new Exercise("Bench Press",      "Chest",      ""));
        dao.insert(new Exercise("Squat",             "Legs",       ""));
        dao.insert(new Exercise("Deadlift",          "Back",       ""));
        dao.insert(new Exercise("Barbell Row",       "Back",       ""));
        dao.insert(new Exercise("Overhead Press",    "Shoulders",  ""));

        // Chest
        dao.insert(new Exercise("Incline Bench Press",   "Chest",     ""));
        dao.insert(new Exercise("Cable Fly",             "Chest",     ""));
        dao.insert(new Exercise("Dumbbell Fly",          "Chest",     ""));

        // Back
        dao.insert(new Exercise("Pull Up",               "Back",      ""));
        dao.insert(new Exercise("Lat Pulldown",          "Back",      ""));
        dao.insert(new Exercise("Seated Cable Row",      "Back",      ""));

        // Legs
        dao.insert(new Exercise("Romanian Deadlift",     "Legs",      ""));
        dao.insert(new Exercise("Leg Press",             "Legs",      ""));
        dao.insert(new Exercise("Leg Curl",              "Legs",      ""));

        // Shoulders
        dao.insert(new Exercise("Lateral Raise",         "Shoulders", ""));
        dao.insert(new Exercise("Face Pull",             "Shoulders", ""));
        dao.insert(new Exercise("Arnold Press",          "Shoulders", ""));

        // Arms
        dao.insert(new Exercise("Barbell Curl",          "Arms",      ""));
        dao.insert(new Exercise("Hammer Curl",           "Arms",      ""));
        dao.insert(new Exercise("Tricep Pushdown",       "Arms",      ""));
        dao.insert(new Exercise("Skull Crusher",         "Arms",      ""));
        dao.insert(new Exercise("Overhead Tricep Ext.",  "Arms",      ""));
        dao.insert(new Exercise("Preacher Curl",         "Arms",      ""));

        // Core
        dao.insert(new Exercise("Plank",                 "Core",      ""));
        dao.insert(new Exercise("Cable Crunch",          "Core",      ""));
        dao.insert(new Exercise("Hanging Leg Raise",     "Core",      ""));
    }
}