package me.chandankumar.workouttracker.ui;


import static me.chandankumar.workouttracker.R.drawable.background_img;
import static me.chandankumar.workouttracker.R.drawable.background_img_blue;
import static me.chandankumar.workouttracker.R.drawable.background_img_dark;
import static me.chandankumar.workouttracker.R.drawable.background_img_pink;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;
import java.util.List;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.adapter.RepInfoAdapter;
import me.chandankumar.workouttracker.database.AppExecutors;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.domain.RepInfo;
import me.chandankumar.workouttracker.utils.Constants;
import me.chandankumar.workouttracker.utils.SharedPref;
import me.chandankumar.workouttracker.utils.Theme;
import me.chandankumar.workouttracker.utils.ThemeColor;
import me.chandankumar.workouttracker.utils.Utils;

public class RepInfoActivity extends AppCompatActivity {

    private TextView exerciseNameTextView;
    private CalendarView workoutCalendarView;
    private RecyclerView repInfoRecyclerview;

    private WorkoutDatabase workoutDatabase;
    private RepInfoAdapter repInfoAdapter;
    private int exerciseId;
    private int bodyPartId;
    private String exerciseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_info);

        setupTheme();
        initViews();

        exerciseId = getIntent().getIntExtra("exerciseId", 1);
        bodyPartId = getIntent().getIntExtra("bodyPartId", 1);
        exerciseName = getIntent().getStringExtra("exerciseName");

        exerciseNameTextView.setText(exerciseName + " - Rep Info");


        workoutDatabase = WorkoutDatabase.getInstance(getApplicationContext());
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

    public void initViews(){
        repInfoRecyclerview = findViewById(R.id.rep_info_recyclerview);
        workoutCalendarView = findViewById(R.id.workout_calendarview);

        exerciseNameTextView = findViewById(R.id.exercise_name_textview);

        workoutCalendarView.setFirstDayOfWeek(2);

        workoutCalendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {


            Date date = new Date(year-Constants.GREGORIAN_INITIAL_YEAR, month, dayOfMonth);

            AppExecutors.getInstance().diskIO().execute(() -> {
                List<RepInfo> reps = workoutDatabase.repInfoDao().getAllByDateAndExerciseId(date, exerciseId);
                runOnUiThread(() -> repInfoAdapter.refresh(reps));
            });
        });
    }

    private void initExerciseRecyclerView(){

        AppExecutors.getInstance().diskIO().execute(() -> {

//            Date date = new Date();
//            int year = date.getYear();
//            int month = date.getMonth();
//            int day = date.getDate();
          //  Date newDateWithoutTime = new Date(year, month, day);

            Date newDateWithoutTime = Utils.getDateWithoutTime();

            List<RepInfo> reps = workoutDatabase.repInfoDao().getAllByDateAndExerciseId(newDateWithoutTime, exerciseId);

            repInfoAdapter = new RepInfoAdapter(this, reps, workoutDatabase);
            repInfoRecyclerview.setHasFixedSize(true);
            repInfoRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            repInfoRecyclerview.setAdapter(repInfoAdapter);

        });
    }

    public void addRepInfo(View view) {

        showAddRepDialog();
    }


    private void showAddRepDialog(){
        View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.rep_info_input_dialog, null , false);

        final EditText weightEditText = viewInflated.findViewById(R.id.weight);
        final EditText repsEditText = viewInflated.findViewById(R.id.reps);


        AlertDialog alertDialog = new MaterialAlertDialogBuilder(RepInfoActivity.this)
                .setTitle("Add Exercise")
                .setView(viewInflated)
                .setPositiveButton(Constants.YES, null)
                .setNegativeButton(Constants.NO, (dialogInterface, i) -> {
                })
                .show();


        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {

            String weight = weightEditText.getText().toString().trim();
            String reps = repsEditText.getText().toString().trim();

            if(weight.isEmpty()){
                weightEditText.setError("Weight cannot be empty");
                return;
            }

            if(Float.parseFloat(weight) < 1 || Float.parseFloat(weight) > 500){
                weightEditText.setError("Invalid weight range 1 - 500 Kg");
                return;
            }

            if(reps.isEmpty()){
                repsEditText.setError("Reps cannot be empty");
                return;
            }

            if(Integer.parseInt(reps) <= 0 || Integer.parseInt(reps) > 50){
                repsEditText.setError("Invalid reps range 1 - 50");
                return;
            }


            AppExecutors.getInstance().diskIO().execute(() -> {


//                Date date = new Date();
//                int year = date.getYear();
//                int month = date.getMonth();
//                int day = date.getDate();
//                Date toBeSaved = new Date(year, month, day);

                Date toBeSaved = Utils.getDateWithoutTime();

                workoutDatabase.repInfoDao().save(new RepInfo(bodyPartId,
                        exerciseId,
                        toBeSaved,
                        Float.parseFloat(weight),
                        Integer.parseInt(reps)));

                List<RepInfo> repInfos = workoutDatabase.repInfoDao().getAllByDateAndExerciseId(toBeSaved, exerciseId);
                runOnUiThread(() -> repInfoAdapter.refresh(repInfos));

                alertDialog.dismiss();

            });
        });

    }


    public void showAddRepInfoDialog(View view) {
        showAddRepDialog();
    }
}