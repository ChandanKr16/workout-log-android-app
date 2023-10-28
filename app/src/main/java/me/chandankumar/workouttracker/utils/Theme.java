package me.chandankumar.workouttracker.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;


public class Theme {

    public enum ThemeColor {
        GREEN,
        BLUE,
        PINK
    }

    public static void changeBackground(Context context, View view, Window window, Drawable background, int colorId){

        view.setBackground(background);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(context, colorId));
    }



}
