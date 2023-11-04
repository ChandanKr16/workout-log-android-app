package me.chandankumar.workouttracker.ui;

import static me.chandankumar.workouttracker.R.drawable.background_img;
import static me.chandankumar.workouttracker.R.drawable.background_img_blue;
import static me.chandankumar.workouttracker.R.drawable.background_img_dark;
import static me.chandankumar.workouttracker.R.drawable.background_img_pink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.raphaelebner.roomdatabasebackup.core.RoomBackup;
import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.utils.BodyParts;
import me.chandankumar.workouttracker.utils.SharedPref;
import me.chandankumar.workouttracker.utils.Theme;
import me.chandankumar.workouttracker.utils.ThemeColor;


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

        if(background == ThemeColor.GREEN.ordinal()){
            Theme.changeBackground(getApplicationContext(), findViewById(R.id.nested_scrollview), this.getWindow(),
                    getResources().getDrawable(background_img), R.color.green);
        }
        if(background == ThemeColor.BLUE.ordinal()){
            Theme.changeBackground(getApplicationContext(), findViewById(R.id.nested_scrollview), this.getWindow(),
                    getResources().getDrawable(background_img_blue), R.color.blue);
        }
        if(background == ThemeColor.PINK.ordinal()){
            Theme.changeBackground(getApplicationContext(), findViewById(R.id.nested_scrollview), this.getWindow(),
                    getResources().getDrawable(background_img_pink), R.color.pink);
        }
        if(background == ThemeColor.DARK.ordinal()){
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

}