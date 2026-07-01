package com.dropsett.app.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dropsett.app.model.Exercise;

import java.util.List;

@Dao
public interface ExerciseDao {

    @Insert
    long insert(Exercise exercise);

    @Update
    void update(Exercise exercise);

    @Delete
    void delete(Exercise exercise);

    @Query("SELECT * FROM exercises ORDER BY name ASC")
    LiveData<List<Exercise>> getAllExercises();

    @Query("SELECT * FROM exercises WHERE equipmentType IN (:types) ORDER BY name ASC")
    LiveData<List<Exercise>> getExercisesByEquipment(List<String> types);

    @Query("SELECT * FROM exercises WHERE equipmentType IN (:types) ORDER BY name ASC")
    List<Exercise> getExercisesByEquipmentSync(List<String> types);

    @Query("SELECT * FROM exercises WHERE muscleGroup = :muscle " +
            "AND equipmentType IN (:types) ORDER BY name ASC")
    List<Exercise> getByMuscleAndEquipment(String muscle, List<String> types);

    @Query("SELECT * FROM exercises WHERE id = :id")
    Exercise getById(long id);

    @Query("SELECT DISTINCT muscleGroup FROM exercises ORDER BY muscleGroup ASC")
    List<String> getAllMuscleGroups();

    @Query("SELECT DISTINCT se.exerciseId FROM session_exercises se " +
            "ORDER BY se.sessionId DESC LIMIT 20")
    List<Long> getRecentlyUsedExerciseIds();
}