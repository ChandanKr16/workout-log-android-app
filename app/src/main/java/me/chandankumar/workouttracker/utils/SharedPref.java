package me.chandankumar.workouttracker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private SharedPref(){
    }


    public static boolean updateBackground(Activity activity, int themeColor){
        SharedPreferences sharedPref = activity.getSharedPreferences("themedata" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Constants.BACKGROUND_KEY, themeColor);
        return editor.commit();
    }


    public static int getBackground(Activity activity){
        SharedPreferences sharedPref = activity.getSharedPreferences("themedata", Context.MODE_APPEND);
        return  sharedPref.getInt(Constants.BACKGROUND_KEY, Constants.DEFAULT_BACKGROUND);
    }


}
