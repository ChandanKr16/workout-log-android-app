package me.chandankumar.workouttracker.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class SettingActivity extends AppCompatActivity {

    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private RoomBackup roomBackup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        roomBackup = new RoomBackup(SettingActivity.this);


        setupBottomSheetView();
    }

    public void showCreditsSheet(View view) {
        bottomSheetDialog.show();
    }

    private void setupBottomSheetView(){
        bottomSheetDialog = new BottomSheetDialog(
                SettingActivity.this, R.style.BottomSheetDialogTheme);

        bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.credits_bottom_sheet,
                        (LinearLayout)findViewById(R.id.bottom_sheet_container));


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
}