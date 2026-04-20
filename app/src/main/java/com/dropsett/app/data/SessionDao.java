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

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    List<WorkoutSession> getAllSessionsSync();

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    WorkoutSession getSessionById(long sessionId);

    @Query("SELECT * FROM workout_sessions WHERE planId = :planId ORDER BY date DESC LIMIT 1")
    WorkoutSession getLastSessionForPlan(long planId);

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC LIMIT 1")
    WorkoutSession getLastSession();

    @Query("SELECT * FROM session_exercises WHERE sessionId = :sessionId ORDER BY sortOrder ASC")
    List<SessionExercise> getExercisesForSession(long sessionId);

    @Query("SELECT * FROM exercise_sets WHERE sessionExerciseId = :sessionExerciseId ORDER BY setIndex ASC")
    List<ExerciseSet> getSetsForSessionExercise(long sessionExerciseId);

    @Query("SELECT se.* FROM session_exercises se " +
            "INNER JOIN workout_sessions ws ON se.sessionId = ws.id " +
            "WHERE se.exerciseId = :exerciseId " +
            "ORDER BY ws.date DESC LIMIT 1")
    SessionExercise getLastSessionExercise(long exerciseId);
    @Query("SELECT ws.* FROM workout_sessions ws " +
            "INNER JOIN session_exercises se ON se.sessionId = ws.id " +
            "WHERE se.id = :sessionExerciseId LIMIT 1")
    WorkoutSession getSessionByExerciseSetId(long sessionExerciseId);

    @Query("SELECT es.* FROM exercise_sets es " +
            "INNER JOIN session_exercises se ON es.sessionExerciseId = se.id " +
            "WHERE se.exerciseId = :exerciseId " +
            "ORDER BY es.sessionExerciseId DESC")
    List<ExerciseSet> getAllSetsForExercise(long exerciseId);

    @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
    void deleteSession(long sessionId);
}