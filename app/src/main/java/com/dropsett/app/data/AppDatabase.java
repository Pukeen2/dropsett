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
        version = 4,
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
                                    seedAsync(context);
                                }

                                @Override
                                public void onDestructiveMigration(
                                        @NonNull SupportSQLiteDatabase db) {
                                    super.onDestructiveMigration(db);
                                    seedAsync(context);
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void seedAsync(Context context) {
        Executors.newSingleThreadExecutor().execute(() ->
                seedExercises(AppDatabase.getInstance(context)));
    }

    private static void seedExercises(AppDatabase db) {
        ExerciseDao dao = db.exerciseDao();

        // ── CHEST ──────────────────────────────────────────────
        dao.insert(new Exercise("Bench Press",
                "Chest", "Triceps, Shoulders",
                "Push", "Barbell", true, ""));
        dao.insert(new Exercise("Incline Bench Press",
                "Chest", "Triceps, Shoulders",
                "Push", "Barbell", true, ""));
        dao.insert(new Exercise("Decline Bench Press",
                "Chest", "Triceps",
                "Push", "Barbell", true, ""));
        dao.insert(new Exercise("Dumbbell Bench Press",
                "Chest", "Triceps, Shoulders",
                "Push", "Dumbbell", true, ""));
        dao.insert(new Exercise("Incline Dumbbell Press",
                "Chest", "Shoulders",
                "Push", "Dumbbell", true, ""));
        dao.insert(new Exercise("Dumbbell Fly",
                "Chest", "",
                "Push", "Dumbbell", false, ""));
        dao.insert(new Exercise("Cable Fly",
                "Chest", "",
                "Push", "Cable", false, ""));
        dao.insert(new Exercise("Low Cable Fly",
                "Chest", "",
                "Push", "Cable", false, ""));
        dao.insert(new Exercise("Chest Dip",
                "Chest", "Triceps",
                "Push", "Bodyweight", true, "Lean forward to target chest"));
        dao.insert(new Exercise("Push Up",
                "Chest", "Triceps, Shoulders",
                "Push", "Bodyweight", true, ""));
        dao.insert(new Exercise("Machine Chest Press",
                "Chest", "Triceps",
                "Push", "Machine", true, ""));
        dao.insert(new Exercise("Pec Deck",
                "Chest", "",
                "Push", "Machine", false, ""));

        // ── BACK ───────────────────────────────────────────────
        dao.insert(new Exercise("Deadlift",
                "Back", "Legs, Glutes",
                "Hinge", "Barbell", true, ""));
        dao.insert(new Exercise("Barbell Row",
                "Back", "Biceps",
                "Pull", "Barbell", true, ""));
        dao.insert(new Exercise("Pendlay Row",
                "Back", "Biceps",
                "Pull", "Barbell", true, "Dead stop each rep"));
        dao.insert(new Exercise("Pull Up",
                "Back", "Biceps",
                "Pull", "Bodyweight", true, ""));
        dao.insert(new Exercise("Chin Up",
                "Back", "Biceps",
                "Pull", "Bodyweight", true, "Supinated grip"));
        dao.insert(new Exercise("Lat Pulldown",
                "Back", "Biceps",
                "Pull", "Cable", true, ""));
        dao.insert(new Exercise("Seated Cable Row",
                "Back", "Biceps",
                "Pull", "Cable", true, ""));
        dao.insert(new Exercise("Single Arm Dumbbell Row",
                "Back", "Biceps",
                "Pull", "Dumbbell", true, ""));
        dao.insert(new Exercise("Face Pull",
                "Back", "Shoulders",
                "Pull", "Cable", false, "External rotation focus"));
        dao.insert(new Exercise("Straight Arm Pulldown",
                "Back", "",
                "Pull", "Cable", false, ""));
        dao.insert(new Exercise("Machine Row",
                "Back", "Biceps",
                "Pull", "Machine", true, ""));
        dao.insert(new Exercise("T-Bar Row",
                "Back", "Biceps",
                "Pull", "Barbell", true, ""));

        // ── LEGS ───────────────────────────────────────────────
        dao.insert(new Exercise("Squat",
                "Legs", "Glutes",
                "Squat", "Barbell", true, ""));
        dao.insert(new Exercise("Front Squat",
                "Legs", "Glutes",
                "Squat", "Barbell", true, "More quad dominant"));
        dao.insert(new Exercise("Romanian Deadlift",
                "Legs", "Glutes",
                "Hinge", "Barbell", true, ""));
        dao.insert(new Exercise("Leg Press",
                "Legs", "Glutes",
                "Squat", "Machine", true, ""));
        dao.insert(new Exercise("Hack Squat",
                "Legs", "",
                "Squat", "Machine", true, ""));
        dao.insert(new Exercise("Leg Curl",
                "Legs", "",
                "Hinge", "Machine", false, ""));
        dao.insert(new Exercise("Leg Extension",
                "Legs", "",
                "Other", "Machine", false, ""));
        dao.insert(new Exercise("Bulgarian Split Squat",
                "Legs", "Glutes",
                "Squat", "Dumbbell", true, ""));
        dao.insert(new Exercise("Lunge",
                "Legs", "Glutes",
                "Squat", "Dumbbell", true, ""));
        dao.insert(new Exercise("Walking Lunge",
                "Legs", "Glutes",
                "Squat", "Dumbbell", true, ""));
        dao.insert(new Exercise("Hip Thrust",
                "Legs", "Glutes",
                "Hinge", "Barbell", true, ""));
        dao.insert(new Exercise("Glute Bridge",
                "Legs", "Glutes",
                "Hinge", "Bodyweight", false, ""));
        dao.insert(new Exercise("Calf Raise",
                "Legs", "",
                "Other", "Machine", false, ""));
        dao.insert(new Exercise("Seated Calf Raise",
                "Legs", "",
                "Other", "Machine", false, ""));
        dao.insert(new Exercise("Goblet Squat",
                "Legs", "Glutes",
                "Squat", "Kettlebell", true, ""));

        // ── SHOULDERS ──────────────────────────────────────────
        dao.insert(new Exercise("Overhead Press",
                "Shoulders", "Triceps",
                "Push", "Barbell", true, ""));
        dao.insert(new Exercise("Seated Dumbbell Press",
                "Shoulders", "Triceps",
                "Push", "Dumbbell", true, ""));
        dao.insert(new Exercise("Arnold Press",
                "Shoulders", "Triceps",
                "Push", "Dumbbell", true, ""));
        dao.insert(new Exercise("Lateral Raise",
                "Shoulders", "",
                "Other", "Dumbbell", false, ""));
        dao.insert(new Exercise("Cable Lateral Raise",
                "Shoulders", "",
                "Other", "Cable", false, ""));
        dao.insert(new Exercise("Machine Lateral Raise",
                "Shoulders", "",
                "Other", "Machine", false, ""));
        dao.insert(new Exercise("Rear Delt Fly",
                "Shoulders", "",
                "Pull", "Dumbbell", false, ""));
        dao.insert(new Exercise("Cable Rear Delt Fly",
                "Shoulders", "",
                "Pull", "Cable", false, ""));
        dao.insert(new Exercise("Front Raise",
                "Shoulders", "",
                "Push", "Dumbbell", false, ""));
        dao.insert(new Exercise("Machine Shoulder Press",
                "Shoulders", "Triceps",
                "Push", "Machine", true, ""));
        dao.insert(new Exercise("Band Pull Apart",
                "Shoulders", "Back",
                "Pull", "Band", false, ""));

        // ── ARMS — BICEPS ──────────────────────────────────────
        dao.insert(new Exercise("Barbell Curl",
                "Arms", "",
                "Pull", "Barbell", false, ""));
        dao.insert(new Exercise("EZ Bar Curl",
                "Arms", "",
                "Pull", "Barbell", false, ""));
        dao.insert(new Exercise("Dumbbell Curl",
                "Arms", "",
                "Pull", "Dumbbell", false, ""));
        dao.insert(new Exercise("Hammer Curl",
                "Arms", "Forearms",
                "Pull", "Dumbbell", false, ""));
        dao.insert(new Exercise("Preacher Curl",
                "Arms", "",
                "Pull", "Barbell", false, ""));
        dao.insert(new Exercise("Cable Curl",
                "Arms", "",
                "Pull", "Cable", false, ""));
        dao.insert(new Exercise("Incline Dumbbell Curl",
                "Arms", "",
                "Pull", "Dumbbell", false, "Long head stretch"));
        dao.insert(new Exercise("Concentration Curl",
                "Arms", "",
                "Pull", "Dumbbell", false, ""));

        // ── ARMS — TRICEPS ─────────────────────────────────────
        dao.insert(new Exercise("Tricep Pushdown",
                "Arms", "",
                "Push", "Cable", false, ""));
        dao.insert(new Exercise("Overhead Tricep Extension",
                "Arms", "",
                "Push", "Cable", false, "Long head focus"));
        dao.insert(new Exercise("Skull Crusher",
                "Arms", "",
                "Push", "Barbell", false, ""));
        dao.insert(new Exercise("Close Grip Bench Press",
                "Arms", "Chest",
                "Push", "Barbell", true, ""));
        dao.insert(new Exercise("Tricep Dip",
                "Arms", "Chest",
                "Push", "Bodyweight", true, ""));
        dao.insert(new Exercise("Diamond Push Up",
                "Arms", "Chest",
                "Push", "Bodyweight", false, ""));
        dao.insert(new Exercise("Kickback",
                "Arms", "",
                "Push", "Dumbbell", false, ""));

        // ── CORE ───────────────────────────────────────────────
        dao.insert(new Exercise("Plank",
                "Core", "",
                "Core", "Bodyweight", false, ""));
        dao.insert(new Exercise("Side Plank",
                "Core", "",
                "Core", "Bodyweight", false, ""));
        dao.insert(new Exercise("Cable Crunch",
                "Core", "",
                "Core", "Cable", false, ""));
        dao.insert(new Exercise("Hanging Leg Raise",
                "Core", "",
                "Core", "Bodyweight", false, ""));
        dao.insert(new Exercise("Ab Wheel Rollout",
                "Core", "",
                "Core", "Bodyweight", false, ""));
        dao.insert(new Exercise("Crunch",
                "Core", "",
                "Core", "Bodyweight", false, ""));
        dao.insert(new Exercise("Reverse Crunch",
                "Core", "",
                "Core", "Bodyweight", false, ""));
        dao.insert(new Exercise("Bicycle Crunch",
                "Core", "",
                "Core", "Bodyweight", false, ""));
        dao.insert(new Exercise("Russian Twist",
                "Core", "",
                "Core", "Bodyweight", false, ""));
        dao.insert(new Exercise("Dead Bug",
                "Core", "",
                "Core", "Bodyweight", false, ""));
        dao.insert(new Exercise("Pallof Press",
                "Core", "",
                "Core", "Cable", false, "Anti-rotation"));
        dao.insert(new Exercise("Hollow Body Hold",
                "Core", "",
                "Core", "Bodyweight", false, ""));

        // ── FULL BODY / COMPOUND ───────────────────────────────
        dao.insert(new Exercise("Power Clean",
                "Back", "Legs, Shoulders",
                "Hinge", "Barbell", true, ""));
        dao.insert(new Exercise("Clean and Press",
                "Shoulders", "Legs, Back",
                "Push", "Barbell", true, ""));
        dao.insert(new Exercise("Kettlebell Swing",
                "Legs", "Back, Core",
                "Hinge", "Kettlebell", true, ""));
        dao.insert(new Exercise("Turkish Get Up",
                "Core", "Shoulders",
                "Other", "Kettlebell", true, ""));
        dao.insert(new Exercise("Farmer's Carry",
                "Core", "Forearms, Traps",
                "Carry", "Dumbbell", true, ""));
        dao.insert(new Exercise("Suitcase Carry",
                "Core", "Forearms",
                "Carry", "Kettlebell", true, ""));
        dao.insert(new Exercise("Thruster",
                "Legs", "Shoulders",
                "Push", "Barbell", true, ""));
        dao.insert(new Exercise("Burpee",
                "Core", "Chest, Legs",
                "Other", "Bodyweight", true, ""));

        // ── BAND SPECIFIC ──────────────────────────────────────
        dao.insert(new Exercise("Band Squat",
                "Legs", "Glutes",
                "Squat", "Band", true, ""));
        dao.insert(new Exercise("Band Pull Through",
                "Legs", "Glutes",
                "Hinge", "Band", true, ""));
        dao.insert(new Exercise("Band Row",
                "Back", "Biceps",
                "Pull", "Band", true, ""));
        dao.insert(new Exercise("Band Chest Press",
                "Chest", "Triceps",
                "Push", "Band", true, ""));
        dao.insert(new Exercise("Band Lateral Raise",
                "Shoulders", "",
                "Other", "Band", false, ""));
        dao.insert(new Exercise("Band Curl",
                "Arms", "",
                "Pull", "Band", false, ""));
        dao.insert(new Exercise("Band Tricep Pushdown",
                "Arms", "",
                "Push", "Band", false, ""));
        dao.insert(new Exercise("Band Glute Kickback",
                "Legs", "Glutes",
                "Other", "Band", false, ""));
    }
}