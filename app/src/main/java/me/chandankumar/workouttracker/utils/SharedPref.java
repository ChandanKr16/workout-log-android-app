package me.chandankumar.workouttracker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private SharedPref(){
    }


    public static void updateBackground(Activity activity, String background){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.BACKGROUND_KEY, background);
        editor.apply();
    }


    public static String getBackground(Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return  sharedPref.getString(Constants.BACKGROUND_KEY, Constants.DEFAULT_BACKGROUND);
    }


}
