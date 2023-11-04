package me.chandankumar.workouttracker.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

import me.chandankumar.workouttracker.domain.WeightLog;

@Dao
public interface WeightLogDao {

    @Query("SELECT * FROM WeightLog ORDER BY date DESC")
    List<WeightLog> getAll();

    @Query("SELECT * FROM WeightLog ORDER BY date DESC LIMIT 1")
    WeightLog getLatestWeight();


    @Query("SELECT * FROM WeightLog WHERE date=:date")
    WeightLog getTodayWeightLog(Date date);

    @Insert
    void save(WeightLog weightLog);

    @Update
    void update(WeightLog weightLog);

    @Delete
    void delete(WeightLog weightLog);

}
