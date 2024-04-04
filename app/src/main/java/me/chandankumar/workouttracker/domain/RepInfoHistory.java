package me.chandankumar.workouttracker.domain;


import androidx.room.Embedded;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


//@Entity(tableName = "RepInfoHistory")
public class RepInfoHistory {

    public Date date;

    @Embedded
    public ArrayList<RepInfo> repList = new ArrayList<>();

    public RepInfoHistory() {
    }

//    public RepInfoHistory(Date date, ArrayList<RepInfo> repList) {
//        this.date = date;
//        this.repList = repList;
//    }
}
