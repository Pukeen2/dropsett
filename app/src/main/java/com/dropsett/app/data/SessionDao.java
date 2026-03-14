package com.dropsett.app.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.dropsett.app.model.ExerciseSet;
import com.dropsett.app.model.SessionExercise;
import com.dropsett.app.model.WorkoutSession;

import java.util.List;

@Dao
public interface SessionDao {

    @Insert
    long insertSession(WorkoutSession session);

    @Insert
    long insertSessionExercise(SessionExercise sessionExercise);

    @Insert
    void insertSet(ExerciseSet set);

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    LiveData<List<WorkoutSession>> getAllSessions();

    @Query("SELECT * FROM session_exercises WHERE sessionId = :sessionId ORDER BY sortOrder ASC")
    List<SessionExercise> getExercisesForSession(long sessionId);

    @Query("SELECT * FROM exercise_sets WHERE sessionExerciseId = :sessionExerciseId ORDER BY setIndex ASC")
    List<ExerciseSet> getSetsForSessionExercise(long sessionExerciseId);

    // Used for last-time hints — finds the most recent session containing this exercise
    @Query("SELECT se.* FROM session_exercises se " +
            "INNER JOIN workout_sessions ws ON se.sessionId = ws.id " +
            "WHERE se.exerciseId = :exerciseId " +
            "ORDER BY ws.date DESC LIMIT 1")
    SessionExercise getLastSessionExercise(long exerciseId);

    // Per-exercise history for the history screen
    @Query("SELECT es.* FROM exercise_sets es " +
            "INNER JOIN session_exercises se ON es.sessionExerciseId = se.id " +
            "WHERE se.exerciseId = :exerciseId " +
            "ORDER BY es.sessionExerciseId DESC")
    List<ExerciseSet> getAllSetsForExercise(long exerciseId);
}