package me.chandankumar.workouttracker.utils;

import android.app.Activity;


import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AlertDialogBuilder {

    public static MaterialAlertDialogBuilder getConfirmAlertDialog(Activity activity, String title, String message){
        return new MaterialAlertDialogBuilder(activity)
                .setTitle(title)
                .setMessage(message);
    }
}
