package me.chandankumar.workouttracker.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import me.chandankumar.workouttracker.dao.ExerciseDao;
import me.chandankumar.workouttracker.dao.RepInfoDao;
import me.chandankumar.workouttracker.domain.Exercise;
import me.chandankumar.workouttracker.domain.RepInfo;
import me.chandankumar.workouttracker.domain.TotalVolume;
import me.chandankumar.workouttracker.utils.Constants;
import me.chandankumar.workouttracker.utils.Converters;

@Database(entities = {Exercise.class, RepInfo.class}, exportSchema = false, version = 1)
@TypeConverters({Converters.class})
public abstract class WorkoutDatabase extends RoomDatabase {


    private static WorkoutDatabase workoutDatabaseInstance;

    public static synchronized WorkoutDatabase getInstance(Context context){
        if(workoutDatabaseInstance == null){
            workoutDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(), WorkoutDatabase.class, Constants.DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return workoutDatabaseInstance;
    }

    public abstract ExerciseDao exerciseDao();

    public abstract RepInfoDao repInfoDao();

}
