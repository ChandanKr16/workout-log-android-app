package me.chandankumar.workouttracker.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import me.chandankumar.workouttracker.domain.Exercise;
import me.chandankumar.workouttracker.domain.RepInfo;
import me.chandankumar.workouttracker.domain.TotalVolume;

@Dao
public interface RepInfoDao {

    @Query("SELECT * FROM RepInfo WHERE exerciseId=:exerciseId ORDER BY date DESC")
    List<RepInfo> getAll(int exerciseId);


    @Query("SELECT SUM( rep * weight) AS totalVolume, date FROM RepInfo WHERE exerciseId=:exerciseId AND date BETWEEN :startDate AND :endDate GROUP BY date")
    List<TotalVolume> getAllBetweenStartDateAndEndDate(int exerciseId, Date startDate, Date endDate);


    @Query("SELECT * FROM RepInfo WHERE exerciseId=:exerciseId AND date=:date ORDER BY date DESC")
    List<RepInfo> getAllByDateAndExerciseId(Date date, int exerciseId);

    @Query("SELECT  SUM( rep * weight) AS totalVolume, date FROM RepInfo WHERE exerciseId=:exerciseId GROUP BY date ORDER BY date")
    List<TotalVolume> getAllByVolume(int exerciseId);

    @Insert
    void save(RepInfo repInfo);

    @Update
    void update(RepInfo repInfo);

    @Delete
    void delete(RepInfo repInfo);

}