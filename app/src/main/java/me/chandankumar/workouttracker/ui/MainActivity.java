package me.chandankumar.workouttracker.ui;

import static me.chandankumar.workouttracker.R.drawable.background_img;
import static me.chandankumar.workouttracker.R.drawable.background_img_blue;
import static me.chandankumar.workouttracker.R.drawable.background_img_dark;
import static me.chandankumar.workouttracker.R.drawable.background_img_pink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import de.raphaelebner.roomdatabasebackup.core.RoomBackup;
import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.utils.BodyParts;
import me.chandankumar.workouttracker.utils.SharedPref;
import me.chandankumar.workouttracker.utils.Theme;


public class MainActivity extends AppCompatActivity {

    private RoomBackup roomBackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupTheme();

        roomBackup = new RoomBackup(MainActivity.this);
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

    public void showBicepExerciseListActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
        intent.putExtra("id", BodyParts.BICEP.ordinal());
        startActivity(intent);
    }

    public void showChestExerciseListActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
        intent.putExtra("id", BodyParts.CHEST.ordinal());
        startActivity(intent);
    }

    public void showTricepsExerciseListActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
        intent.putExtra("id", BodyParts.TRICEPS.ordinal());
        startActivity(intent);
    }

    public void showBackExerciseListActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
        intent.putExtra("id", BodyParts.BACK.ordinal());
        startActivity(intent);
    }

    public void showShouldersExerciseListActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
        intent.putExtra("id", BodyParts.SHOULDERS.ordinal());
        startActivity(intent);
    }

    public void showAbdominalExerciseListActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
        intent.putExtra("id", BodyParts.ABDOMINAL.ordinal());
        startActivity(intent);
    }

    public void showForearmsExerciseListActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
        intent.putExtra("id", BodyParts.FOREARMS.ordinal());
        startActivity(intent);
    }

    public void showLegExerciseListActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
        intent.putExtra("id", BodyParts.LEG.ordinal());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.weight_log:
                showWeightLogActivity();
                return true;

            case R.id.backup_db:
                backup();
                return true;
            case R.id.restore_db:
                restore();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showWeightLogActivity() {
        startActivity(new Intent(MainActivity.this, WeightLogActivity.class));
    }

    private void backup(){
        roomBackup.backupLocation(RoomBackup.BACKUP_FILE_LOCATION_CUSTOM_DIALOG);
        roomBackup.backupLocationCustomFile(new File(this.getFilesDir()+"/databasebackup/workoutdb.sqlite3"));
        roomBackup.database(WorkoutDatabase.getInstance(getApplicationContext()));
        roomBackup.maxFileCount(5);
        roomBackup.onCompleteListener((success, message, exitCode) -> {

            if (success){
                Toast.makeText(getApplicationContext(), "Backup Completed", Toast.LENGTH_LONG).show();
                roomBackup.restartApp(new Intent(getApplicationContext(), MainActivity.class));
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
                roomBackup.restartApp(new Intent(getApplicationContext(), MainActivity.class));
            }else{
                Toast.makeText(getApplicationContext(), "Restoration failed: " + message, Toast.LENGTH_LONG).show();
            }
        });
        roomBackup.restore();
    }

}