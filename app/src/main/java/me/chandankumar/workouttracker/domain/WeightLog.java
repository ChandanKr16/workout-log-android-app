package me.chandankumar.workouttracker.domain;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity(tableName = "WeightLog")
public class WeightLog {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "weightId")
    private int weightId;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "weight")
    private float weight;

    @ColumnInfo(name = "gain")
    private float gain;

    public WeightLog() {
    }

    @Ignore
    public WeightLog(int weightId, Date date, float weight, float gain) {
        this.weightId = weightId;
        this.date = date;
        this.weight = weight;
        this.gain = gain;
    }

    @Ignore
    public WeightLog(Date date, float weight, float gain) {
        this.date = date;
        this.weight = weight;
        this.gain = gain;
    }

    public int getWeightId() {
        return weightId;
    }

    public void setWeightId(int weightId) {
        this.weightId = weightId;
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

    public float getGain() {
        return gain;
    }

    public void setGain(float gain) {
        this.gain = gain;
    }
}
