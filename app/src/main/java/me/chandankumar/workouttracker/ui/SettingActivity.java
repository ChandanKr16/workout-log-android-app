package me.chandankumar.workouttracker.ui;

import static me.chandankumar.workouttracker.R.drawable.background_img;
import static me.chandankumar.workouttracker.R.drawable.background_img_blue;
import static me.chandankumar.workouttracker.R.drawable.background_img_dark;
import static me.chandankumar.workouttracker.R.drawable.background_img_pink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;

import de.raphaelebner.roomdatabasebackup.core.RoomBackup;
import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.utils.SharedPref;
import me.chandankumar.workouttracker.utils.Theme;

public class SettingActivity extends AppCompatActivity {

    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private RoomBackup roomBackup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setupTheme();

        roomBackup = new RoomBackup(SettingActivity.this);


        setupBottomSheetView();
    }

    private void setupTheme(){
        int background = SharedPref.getBackground(this);

        if(background == Theme.ThemeColor.GREEN.ordinal()){
            Theme.changeBackground(getApplicationContext(), findViewById(R.id.nested_scrollview), this.getWindow(),
                    getResources().getDrawable(background_img), R.color.green);
        }
        if(background == Theme.ThemeColor.BLUE.ordinal()){
            Theme.changeBackground(getApplicationContext(), findViewById(R.id.nested_scrollview), this.getWindow(),
                    getResources().getDrawable(background_img_blue), R.color.blue);
        }
        if(background == Theme.ThemeColor.PINK.ordinal()){
            Theme.changeBackground(getApplicationContext(), findViewById(R.id.nested_scrollview), this.getWindow(),
                    getResources().getDrawable(background_img_pink), R.color.pink);
        }
        if(background == Theme.ThemeColor.DARK.ordinal()){
            Theme.changeBackground(getApplicationContext(), findViewById(R.id.nested_scrollview), this.getWindow(),
                    getResources().getDrawable(background_img_dark), R.color.dark);
        }
    }

    public void showCreditsSheet(View view) {
        bottomSheetDialog.show();
    }

    private void setupThemeForBottomSheet(){
        int background = SharedPref.getBackground(this);
        if(background == Theme.ThemeColor.GREEN.ordinal()){
            Theme.changeBackground(getApplicationContext(), bottomSheetView.findViewById(R.id.bottom_sheet_container), this.getWindow(),
                    getResources().getDrawable(background_img), R.color.green);
        }
        if(background == Theme.ThemeColor.BLUE.ordinal()){
            Theme.changeBackground(getApplicationContext(), bottomSheetView.findViewById(R.id.bottom_sheet_container), this.getWindow(),
                    getResources().getDrawable(background_img_blue), R.color.blue);
        }
        if(background == Theme.ThemeColor.PINK.ordinal()){
            Theme.changeBackground(getApplicationContext(), bottomSheetView.findViewById(R.id.bottom_sheet_container), this.getWindow(),
                    getResources().getDrawable(background_img_pink), R.color.pink);
        }
        if(background == Theme.ThemeColor.DARK.ordinal()){
            Theme.changeBackground(getApplicationContext(), bottomSheetView.findViewById(R.id.bottom_sheet_container), this.getWindow(),
                    getResources().getDrawable(background_img_dark), R.color.dark);
        }
    }


    private void setupBottomSheetView(){
        bottomSheetDialog = new BottomSheetDialog(
                SettingActivity.this, R.style.BottomSheetDialogTheme);

        bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.credits_bottom_sheet,
                        (LinearLayout)findViewById(R.id.bottom_sheet_container));

        setupThemeForBottomSheet();


        bottomSheetDialog.setContentView(bottomSheetView);
    }

    private void backup(){
        roomBackup.backupLocation(RoomBackup.BACKUP_FILE_LOCATION_CUSTOM_DIALOG);
        roomBackup.backupLocationCustomFile(new File(this.getFilesDir()+"/databasebackup/workoutdb.sqlite3"));
        roomBackup.database(WorkoutDatabase.getInstance(getApplicationContext()));
        roomBackup.maxFileCount(5);
        roomBackup.onCompleteListener((success, message, exitCode) -> {

            if (success){
                Toast.makeText(getApplicationContext(), "Backup Completed", Toast.LENGTH_LONG).show();
                roomBackup.restartApp(new Intent(getApplicationContext(), HomeActivity.class));
            }
            else{
                Toast.makeText(getApplicationContext(), "Backup failed: " + message, Toast.LENGTH_LONG).show();
            }
        });
        roomBackup.backup();
    }

    public void restore(){
        roomBackup.backupLocation(RoomBackup.BACKUP_FILE_LOCATION_CUSTOM_DIALOG);
        roomBackup.backupLocationCustomFile(new File(this.getFilesDir()+"/databasebackup/workoutdb.sqlite3"));
        roomBackup.database(WorkoutDatabase.getInstance(getApplicationContext()));
        roomBackup.onCompleteListener((success, message, exitCode) -> {

            if (success){
                Toast.makeText(getApplicationContext(), "Restore successful", Toast.LENGTH_LONG).show();
                roomBackup.restartApp(new Intent(getApplicationContext(), HomeActivity.class));
            }else{
                Toast.makeText(getApplicationContext(), "Restoration failed: " + message, Toast.LENGTH_LONG).show();
            }
        });
        roomBackup.restore();
    }

    public void backupWorkoutLog(View view) {
        backup();
    }

    public void restoreWorkoutLog(View view) {
        restore();
    }

    public void reportBug(View view) {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:ckp1606@gmail.com?subject=Workout Log App Bug Report"));
        startActivity(Intent.createChooser(emailIntent, "Report Bug"));
    }

    public void changeBackgroundToGreen(View view) {

        SharedPref.updateBackground(this, Theme.ThemeColor.GREEN.ordinal());
        restartApp();
    }

    public void changeBackgroundToBlue(View view) {
        SharedPref.updateBackground(this,  Theme.ThemeColor.BLUE.ordinal());
        restartApp();

    }

    public void changeBackgroundToPink(View view) {
        SharedPref.updateBackground(this, Theme.ThemeColor.PINK.ordinal());
        restartApp();
    }

    private void restartApp(){
        Intent intent = new Intent(this, HomeActivity.class);
        this.startActivity(intent);
        this.finishAffinity();
    }

    public void changeBackgroundToDark(View view) {
        SharedPref.updateBackground(this, Theme.ThemeColor.DARK.ordinal());
        restartApp();
    }
}