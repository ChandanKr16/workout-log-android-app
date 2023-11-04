package me.chandankumar.workouttracker.domain;



import java.util.Date;


public class TotalVolume {

    private float totalVolume;
    private Date date;

    public TotalVolume(float totalVolume, Date date) {
        this.totalVolume = totalVolume;
        this.date = date;
    }


    public float getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(float totalVolume) {
        this.totalVolume = totalVolume;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TotalVolume{" +
                "totalVolume=" + totalVolume +
                ", date=" + date +
                '}';
    }
}
