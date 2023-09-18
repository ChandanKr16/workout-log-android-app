package me.chandankumar.workouttracker.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import me.chandankumar.workouttracker.domain.RepInfo;
import me.chandankumar.workouttracker.domain.WeightLog;

@Dao
public interface WeightLogDao {

    @Query("SELECT * FROM WeightLog ORDER BY date DESC")
    List<WeightLog> getAll();

    @Insert
    void save(WeightLog weightLog);

    @Update
    void update(WeightLog weightLog);

    @Delete
    void delete(WeightLog weightLog);

}
