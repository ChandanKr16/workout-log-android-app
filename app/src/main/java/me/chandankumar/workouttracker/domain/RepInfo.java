package me.chandankumar.workouttracker.domain;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity(tableName = "RepInfo",
    foreignKeys = {@ForeignKey(entity = Exercise.class,
            parentColumns = {"bodyPartId", "exerciseId"},
            childColumns = {"bodyPartId", "exerciseId"},
            onDelete = ForeignKey.CASCADE)}
)
public class RepInfo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "repId")
    private int repId;

    @ColumnInfo(name = "bodyPartId")
    private int bodyPartId;

    @ColumnInfo(name = "exerciseId")
    private int exerciseId;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "weight")
    private float weight;

    @ColumnInfo(name = "rep")
    private int rep;


    public RepInfo() {
    }

    @Ignore
    public RepInfo(int bodyPartId, int exerciseId, Date date, float weight, int rep) {
        this.bodyPartId = bodyPartId;
        this.exerciseId = exerciseId;
        this.date = date;
        this.weight = weight;
        this.rep = rep;
    }

    @Ignore
    public RepInfo(int repId, int bodyPartId, int exerciseId, Date date, float weight, int rep) {
        this.repId = repId;
        this.bodyPartId = bodyPartId;
        this.exerciseId = exerciseId;
        this.date = date;
        this.weight = weight;
        this.rep = rep;
    }

    public int getRepId() {
        return repId;
    }

    public void setRepId(int repId) {
        this.repId = repId;
    }

    public int getBodyPartId() {
        return bodyPartId;
    }

    public void setBodyPartId(int bodyPartId) {
        this.bodyPartId = bodyPartId;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getRep() {
        return rep;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    @Override
    public String toString() {
        return "RepInfo{" +
                "repId=" + repId +
                ", bodyPartId=" + bodyPartId +
                ", exerciseId=" + exerciseId +
                ", date=" + date +
                ", weight=" + weight +
                ", rep=" + rep +
                '}';
    }
}
