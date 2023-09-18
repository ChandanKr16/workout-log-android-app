package me.chandankumar.workouttracker.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

import me.chandankumar.workouttracker.domain.Exercise;

@Dao
public interface ExerciseDao {

    @Query("SELECT * FROM Exercise WHERE bodyPartId=:bodyPartId")
    List<Exercise> getAllByBodyPartId(int bodyPartId);

    @Query("SELECT * FROM Exercise")
    List<Exercise> getAll();

    @Insert
    void save(Exercise exercise);

    @Update
    void update(Exercise exercise);

    @Delete
    void delete(Exercise exercise);

}
