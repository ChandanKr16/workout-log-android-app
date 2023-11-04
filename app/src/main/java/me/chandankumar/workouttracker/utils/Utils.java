package me.chandankumar.workouttracker.utils;

import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static String constructDate(Date date){
        return "" + date.getDate() + "-" + (date.getMonth()+1) + "-" + (date.getYear()+ Constants.GREGORIAN_INITIAL_YEAR);
    }

    public static double calculate1RM(int reps, float weight){

        double oneRMResult = weight / (1.0278 - (0.0278 * reps));
        return ((double) Math.round(oneRMResult * 100.0) / 100.0);
    }

    public static Date getDateWithoutTime(){
        Date date = new Date();
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDate();
        return new Date(year, month, day);
    }

    public static Calendar getLastWeekCalendarDate(){
        Calendar mCalendar = Calendar.getInstance();

        Date lastWeekDate = new Date();
        lastWeekDate.setTime(lastWeekDate.getTime() - 6 * 86400000L);
        mCalendar.setTime(lastWeekDate);

        return mCalendar;
    }


}
