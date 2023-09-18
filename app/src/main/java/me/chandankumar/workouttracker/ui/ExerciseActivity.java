package me.chandankumar.workouttracker.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.adapter.ExerciseAdapter;
import me.chandankumar.workouttracker.database.AppExecutors;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.domain.Exercise;
import me.chandankumar.workouttracker.utils.BodyParts;
import me.chandankumar.workouttracker.utils.Constants;
import pl.droidsonroids.gif.GifImageView;

public class ExerciseActivity extends AppCompatActivity {

    private RecyclerView exerciseRecyclerview;
    private WorkoutDatabase workoutDatabase;
    private ExerciseAdapter exerciseAdapter;
    private int bodyPartId;
    private GifImageView emptyImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        bodyPartId = getIntent().getIntExtra("id", BodyParts.BICEP.ordinal());
        workoutDatabase = WorkoutDatabase.getInstance(getApplicationContext());

        initView();
        exerciseRecyclerview.setVisibility(View.GONE);

        initExerciseRecyclerView();

    }

    private void initView(){
        exerciseRecyclerview = findViewById(R.id.exercise_recyclerview);
        emptyImg = findViewById(R.id.empty_img);
    }


    private void initExerciseRecyclerView(){

        AppExecutors.getInstance().diskIO().execute(() -> {
            List<Exercise> exercises = workoutDatabase.exerciseDao().getAllByBodyPartId(bodyPartId);

            runOnUiThread(() ->{

                if(exercises.size()>0){
                    emptyImg.setVisibility(View.GONE);
                    exerciseRecyclerview.setVisibility(View.VISIBLE);
                }

                exerciseAdapter = new ExerciseAdapter(this, exercises, workoutDatabase, bodyPartId);
                exerciseRecyclerview.setHasFixedSize(true);
                exerciseRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                exerciseRecyclerview.setAdapter(exerciseAdapter);
            });

        });



    }

    private void showAddExerciseDialog(){
        View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.exercise_input_dialog, null , false);

        final EditText exerciseNameEditText = viewInflated.findViewById(R.id.exercise_name);

        AlertDialog alertDialog = new MaterialAlertDialogBuilder(ExerciseActivity.this)
                .setTitle("Add Exercise")
                .setView(viewInflated)
                .setPositiveButton(Constants.YES, null)
                .setNegativeButton(Constants.NO, (dialogInterface, i) -> {
                })
                .show();


        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            String exerciseName = exerciseNameEditText.getText().toString().trim();

            if(exerciseName.isEmpty() ){
                exerciseNameEditText.setError("Exercise Name cannot be empty");
                return;
            }

            emptyImg.setVisibility(View.GONE);
            exerciseRecyclerview.setVisibility(View.VISIBLE);

            AppExecutors.getInstance().diskIO().execute(() -> {
                workoutDatabase.exerciseDao().save(new Exercise(bodyPartId, exerciseName));
                List<Exercise> exercises = workoutDatabase.exerciseDao().getAllByBodyPartId(bodyPartId);
                runOnUiThread(() -> exerciseAdapter.refresh(exercises));
            });

            alertDialog.dismiss();
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_exercise_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_exercise_menu_item:
                showAddExerciseDialog();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}