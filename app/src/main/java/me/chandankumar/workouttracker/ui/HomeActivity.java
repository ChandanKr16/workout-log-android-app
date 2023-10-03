package me.chandankumar.workouttracker.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import me.chandankumar.workouttracker.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void showWorkoutLogActivity(View view){
        startActivity(new Intent(this, MainActivity.class));
    }

    public void showWeightLogActivity(View view) {
        startActivity(new Intent(this, WeightLogActivity.class));
    }

    public void showStatsActivity(View view) {
        startActivity(new Intent(this, StatsActivity.class));
    }
}