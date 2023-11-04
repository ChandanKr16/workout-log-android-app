package me.chandankumar.workouttracker.domain;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Exercise", indices = {@Index(value = {"exerciseId", "bodyPartId"}, unique = true)})
public class Exercise {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exerciseId")
    private int exerciseId;

    @ColumnInfo(name = "bodyPartId")
    private int bodyPartId;

    @ColumnInfo(name = "exerciseName")
    private String exerciseName;

    @Ignore
    public Exercise() {
    }

    public Exercise(int bodyPartId, String exerciseName) {
        this.bodyPartId = bodyPartId;
        this.exerciseName = exerciseName;
    }

    @Ignore
    public Exercise(int exerciseId, int bodyPartId, String exerciseName) {
        this.exerciseId = exerciseId;
        this.bodyPartId = bodyPartId;
        this.exerciseName = exerciseName;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getBodyPartId() {
        return bodyPartId;
    }

    public void setBodyPartId(int bodyPartId) {
        this.bodyPartId = bodyPartId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }
}
