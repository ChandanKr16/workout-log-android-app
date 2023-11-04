package me.chandankumar.workouttracker.ui;

import static me.chandankumar.workouttracker.R.drawable.background_img;
import static me.chandankumar.workouttracker.R.drawable.background_img_blue;
import static me.chandankumar.workouttracker.R.drawable.background_img_dark;
import static me.chandankumar.workouttracker.R.drawable.background_img_pink;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.adapter.ExerciseAdapter;
import me.chandankumar.workouttracker.database.AppExecutors;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.domain.Exercise;
import me.chandankumar.workouttracker.utils.BodyParts;
import me.chandankumar.workouttracker.utils.Constants;
import me.chandankumar.workouttracker.utils.SharedPref;
import me.chandankumar.workouttracker.utils.Theme;
import me.chandankumar.workouttracker.utils.ThemeColor;

public class ExerciseActivity extends AppCompatActivity {

    private RecyclerView exerciseRecyclerview;
    private WorkoutDatabase workoutDatabase;
    private ExerciseAdapter exerciseAdapter;
    private int bodyPartId;
    private LottieAnimationView emptyImg;
    private FloatingActionButton floatingActionButton;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        initView();

        setupTheme();

        bodyPartId = getIntent().getIntExtra("id", BodyParts.BICEP.ordinal());
        workoutDatabase = WorkoutDatabase.getInstance(getApplicationContext());

        String title = BodyParts.values()[bodyPartId].name();

        titleTextView.setText(""+ title.charAt(0)+"".toUpperCase() + title.substring(1).toLowerCase() + " - Exercise");

        exerciseRecyclerview.setVisibility(View.GONE);

        initExerciseRecyclerView();

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

    private void initView(){
        exerciseRecyclerview = findViewById(R.id.exercise_recyclerview);
        emptyImg = findViewById(R.id.empty_img);
        floatingActionButton = findViewById(R.id.add_fab);
        titleTextView = findViewById(R.id.title_textview);
       // floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));

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

    public void showAddExerciseDialog(View view1){
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
}