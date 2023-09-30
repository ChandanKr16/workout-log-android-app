package me.chandankumar.workouttracker.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;
import java.util.List;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.adapter.WeightLogAdapter;
import me.chandankumar.workouttracker.database.AppExecutors;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.domain.WeightLog;
import me.chandankumar.workouttracker.utils.Constants;

public class WeightLogActivity extends AppCompatActivity {

    private RecyclerView weightLogRecyclerView;
    private WorkoutDatabase workoutDatabase;
    private WeightLogAdapter weightLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_log);

        workoutDatabase = WorkoutDatabase.getInstance(getApplicationContext());
        initViews();

    }

    private void initViews(){
        weightLogRecyclerView = findViewById(R.id.weight_log_recyclerview);


        AppExecutors.getInstance().diskIO().execute(() -> {

            List<WeightLog> logs = workoutDatabase.weightLogDao().getAll();

            runOnUiThread(() -> {
                weightLogAdapter = new WeightLogAdapter(WeightLogActivity.this, logs, workoutDatabase);
                weightLogRecyclerView.setHasFixedSize(true);
                weightLogRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                weightLogRecyclerView.setAdapter(weightLogAdapter);
            });

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_rep_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_exercise_menu_item:
                showAddWeightLogDialog();
                return true;
            case R.id.chart_menu_item:

                showWeightLogGraphActivity();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showWeightLogGraphActivity() {
        startActivity(new Intent(getApplicationContext(), WeightLogGraphActivity.class));
    }

    private void showAddWeightLogDialog() {
        View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.weight_input_dialog, null , false);

        final EditText weightEditText = viewInflated.findViewById(R.id.weight_log_edittext);


        AlertDialog alertDialog = new MaterialAlertDialogBuilder(WeightLogActivity.this)
                .setTitle("Add Weight")
                .setView(viewInflated)
                .setPositiveButton(Constants.YES, null)
                .setNegativeButton(Constants.NO, (dialogInterface, i) -> {
                })
                .show();


        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(view -> {

            AppExecutors.getInstance().diskIO().execute(() -> {

                Date today = new Date();
                int year1 = today.getYear();
                int month1 = today.getMonth();
                int day1 = today.getDate();
                Date today1 = new Date(year1, month1, day1);

                if(workoutDatabase.weightLogDao().getTodayWeightLog(today1) == null) {



                    String weight = weightEditText.getText().toString().trim();

                    if(weight.isEmpty()){
                        weightEditText.setError("Weight cannot be empty");
                        return;
                    }


                    AppExecutors.getInstance().diskIO().execute(() -> {


                        float lastWeightLog = 0f;

                        if(workoutDatabase.weightLogDao().getLatestWeight() != null)
                            lastWeightLog = workoutDatabase.weightLogDao().getLatestWeight().getWeight();


                        Date date = new Date();
                        int year = date.getYear();
                        int month = date.getMonth();
                        int day = date.getDate();
                        Date toBeSaved = new Date(year, month, day);

                        float gain = Float.parseFloat(weight) - lastWeightLog;

                        gain = (float) ((float) Math.round(gain * 100.0) / 100.0);

                        workoutDatabase.weightLogDao().save(new WeightLog(toBeSaved, Float.parseFloat(weight), gain));

                        List<WeightLog> logs = workoutDatabase.weightLogDao().getAll();
                        runOnUiThread(() -> weightLogAdapter.refresh(logs));

                        alertDialog.dismiss();
                });
                }});
        });
    }


}