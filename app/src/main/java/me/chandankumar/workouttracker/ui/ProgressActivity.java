package me.chandankumar.workouttracker.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;


import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.database.AppExecutors;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.domain.TotalVolume;
import me.chandankumar.workouttracker.ui.customviews.CustomEditText;
import me.chandankumar.workouttracker.ui.customviews.DrawableClickListener;


public class ProgressActivity extends AppCompatActivity {

    private int exerciseId;
    private BarChart barChart;
    private List<BarModel> barModelList;
    private CustomEditText startDateEditText;
    private CustomEditText endDateEditText;
    private Button showButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        initViews();

        exerciseId = getIntent().getIntExtra("exerciseId", 0);
        barModelList = new ArrayList<>();

        setRepData();
        barChart.addBarList(barModelList);
        barChart.startAnimation();

        attachListenerOnDatePicker();
    }


    private void initViews(){
        barChart = (BarChart) findViewById(R.id.bar_chart);
        startDateEditText = (CustomEditText) findViewById(R.id.start_date_edit_text);
        endDateEditText = (CustomEditText) findViewById(R.id.end_date_edit_text);
        showButton = (Button) findViewById(R.id.show_button);
    }


    private void attachListenerOnDatePicker(){
        Calendar mCalendar = Calendar.getInstance();

        Date lastWeekDate = new Date();
        lastWeekDate.setTime(lastWeekDate.getTime() - 6 * 86400000L);
        mCalendar.setTime(lastWeekDate);

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        startDateEditText.setText(""+dayOfMonth + "-" + (month+1) + "-" + year);

        DatePickerDialog startDatePickerDialog = new DatePickerDialog(
                ProgressActivity.this,
                (datePicker, year1, month1, dayOfMonth1) -> startDateEditText.setText(""+ dayOfMonth1 + "-" + (month+1) + "-" + year1),
                year, month, dayOfMonth);

        startDateEditText.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if(DrawablePosition.RIGHT == target){
                    startDatePickerDialog.show();
                }
            }
        });

        Date today = new Date();
        endDateEditText.setText(""+today.getDate() + "-" + (today.getMonth() + 1) + "-" + year);

        DatePickerDialog endDatePickerDialog = new DatePickerDialog(
                ProgressActivity.this,
                (datePicker, year1, month1, dayOfMonth1) -> endDateEditText.setText(""+ dayOfMonth1 + "-" + (month+1) + "-" + year1),
                year, today.getMonth(), today.getDate());

        endDateEditText.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if(DrawablePosition.RIGHT == target){
                    endDatePickerDialog.show();
                }
            }
        });


        showButton.setOnClickListener(view -> {


            Date startDate = new Date(startDatePickerDialog.getDatePicker().getYear(), startDatePickerDialog.getDatePicker().getMonth(), startDatePickerDialog.getDatePicker().getDayOfMonth(), 0,0,0);
            Date endDate = new Date(endDatePickerDialog.getDatePicker().getYear(), endDatePickerDialog.getDatePicker().getMonth(), endDatePickerDialog.getDatePicker().getDayOfMonth(),0,0,0);

            startDate.setYear(startDate.getYear() - 1900);
            endDate.setYear(endDate.getYear() - 1900);

            setRepData(exerciseId,
                    startDate,
                    endDate);

        });


    }


    private void setRepData(){

        AppExecutors.getInstance().diskIO().execute(() ->{
            List<TotalVolume> allByVolume = WorkoutDatabase.getInstance(getApplicationContext())
                    .repInfoDao()
                    .getAllByVolume(exerciseId);

            String pattern = "dd-MMM";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            for(int i = 0; i < allByVolume.size(); i++){
                String date = simpleDateFormat.format(allByVolume.get(i).getDate());
                barModelList.add(new BarModel(date, allByVolume.get(i).getTotalVolume(), 0xFF1BA4E6));
            }
        });


    }


    private void setRepData(int exerciseId, Date startDate, Date endDate){

        barChart.clearChart();
        barModelList.clear();

        AppExecutors.getInstance().diskIO().execute(() ->{
            List<TotalVolume> allByVolume = WorkoutDatabase.getInstance(getApplicationContext())
                    .repInfoDao()
                    .getAllBetweenStartDateAndEndDate(exerciseId, startDate, endDate);


            String pattern = "dd-MMM";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            for(int i = 0; i < allByVolume.size(); i++){
                String date = simpleDateFormat.format(allByVolume.get(i).getDate());
                barModelList.add(new BarModel(date, allByVolume.get(i).getTotalVolume(), 0xFF1BA4E6));
            }

            runOnUiThread(() -> {
                if(barModelList.size() > 0) {
                    barChart.addBarList(barModelList);
                    barChart.startAnimation();
                }
            });


        });
    }


}