package me.chandankumar.workouttracker.ui;

import static me.chandankumar.workouttracker.R.drawable.background_img;
import static me.chandankumar.workouttracker.R.drawable.background_img_blue;
import static me.chandankumar.workouttracker.R.drawable.background_img_dark;
import static me.chandankumar.workouttracker.R.drawable.background_img_pink;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.database.AppExecutors;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.domain.Exercise;
import me.chandankumar.workouttracker.domain.RepInfo;
import me.chandankumar.workouttracker.domain.TotalVolume;
import me.chandankumar.workouttracker.ui.customviews.CustomEditText;
import me.chandankumar.workouttracker.ui.customviews.DrawableClickListener;
import me.chandankumar.workouttracker.utils.Constants;
import me.chandankumar.workouttracker.utils.SharedPref;
import me.chandankumar.workouttracker.utils.Theme;
import me.chandankumar.workouttracker.utils.ThemeColor;
import me.chandankumar.workouttracker.utils.Utils;

public class StatsActivity extends AppCompatActivity {


    private PowerSpinnerView muscleGroupSpinner;
    private PowerSpinnerView exerciseSpinner;
    private CustomEditText startDateEditText;
    private CustomEditText endDateEditText;
    private TextView maxWeightTextView;
    private TextView totalVolumeTextView;
    private TextView exerciseCountTextView;
    private Button showButton;

    private BarChart chart;
    private XAxis xAxis;
    private List<Exercise> exerciseList;
    private ArrayList<BarEntry> values;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        setupTheme();

        initViews();
        attachListenerOnMuscleGroupSpinner();
        attachListenerOnDatePickerAndShowButton();
        setBarChartConfig();

        exerciseSpinner.setOnSpinnerItemSelectedListener((i, o, i1, t1) -> exerciseSpinner.setError(null));

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


    private void initViews(){
        chart = findViewById(R.id.bar_chart);
        muscleGroupSpinner = findViewById(R.id.muscle_group_spinner);
        exerciseSpinner = findViewById(R.id.exercise_spinner);

        startDateEditText = (CustomEditText) findViewById(R.id.start_date_edit_text);
        endDateEditText = (CustomEditText) findViewById(R.id.end_date_edit_text);
        showButton = (Button) findViewById(R.id.show_button);
        maxWeightTextView = findViewById(R.id.max_weight_textview);
        totalVolumeTextView = findViewById(R.id.total_volume_textview);
        exerciseCountTextView = findViewById(R.id.exercise_count_textview);

    }

    private void attachListenerOnMuscleGroupSpinner(){
        muscleGroupSpinner.setOnSpinnerItemSelectedListener((i, o, bodyPartId, t1) -> {

            muscleGroupSpinner.setError(null);

            AppExecutors.getInstance().diskIO().execute(() -> {
                List<String> exerciseNameList = WorkoutDatabase.getInstance(getApplicationContext())
                        .exerciseDao()
                        .getAllExerciseNamesByBodyPartId(bodyPartId);

                exerciseList =  WorkoutDatabase.getInstance(getApplicationContext())
                        .exerciseDao()
                        .getAllByBodyPartId(bodyPartId);

                runOnUiThread(() -> {
                    exerciseSpinner.setText("Exercise");

                    if(exerciseList.size()>0){

                        exerciseSpinner.setSpinnerPopupHeight(128*exerciseList.size());

                        exerciseSpinner.setItems(exerciseNameList);
                    }
                    else{
                        exerciseSpinner.setItems(new ArrayList<>());
                    }
                });

            });


        });
    }

    private void attachListenerOnDatePickerAndShowButton(){
//        Calendar mCalendar = Calendar.getInstance();
//
//        Date lastWeekDate = new Date();
//        lastWeekDate.setTime(lastWeekDate.getTime() - 6 * 86400000L);
//        mCalendar.setTime(lastWeekDate);

        Calendar mCalendar = Utils.getLastWeekCalendarDate();

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        startDateEditText.setText(""+dayOfMonth + "-" + (month+1) + "-" + year);

        DatePickerDialog startDatePickerDialog = new DatePickerDialog(
                StatsActivity.this,
                (datePicker, year1, month1, dayOfMonth1) -> startDateEditText.setText(""+ dayOfMonth1 + "-" + (datePicker.getMonth()+1) + "-" + year1),
                year, month, dayOfMonth);

        startDateEditText.setDrawableClickListener(target -> {
            if(DrawableClickListener.DrawablePosition.RIGHT == target){
                startDatePickerDialog.show();
            }
        });

        Date today = new Date();
        endDateEditText.setText(""+today.getDate() + "-" + (today.getMonth() + 1) + "-" + year);

        DatePickerDialog endDatePickerDialog = new DatePickerDialog(
                StatsActivity.this,
                (datePicker, year1, month1, dayOfMonth1) -> endDateEditText.setText(""+ dayOfMonth1 + "-" + (datePicker.getMonth()+1) + "-" + year1),
                year, today.getMonth(), today.getDate());

        endDateEditText.setDrawableClickListener(target -> {
            if(DrawableClickListener.DrawablePosition.RIGHT == target){
                endDatePickerDialog.show();
            }
        });


        showButton.setOnClickListener(view -> {

            if(muscleGroupSpinner.getSelectedIndex() == -1){
                muscleGroupSpinner.setError("Choose muscle group");
                return;
            }

            if(exerciseSpinner.getSelectedIndex() == -1){
                exerciseSpinner.setError("Choose an exercise");
                return;
            }

            Date startDate = new Date(startDatePickerDialog.getDatePicker().getYear() - Constants.GREGORIAN_INITIAL_YEAR,
                    startDatePickerDialog.getDatePicker().getMonth(),
                    startDatePickerDialog.getDatePicker().getDayOfMonth(), 0,0,0);

            Date endDate = new Date(endDatePickerDialog.getDatePicker().getYear() - Constants.GREGORIAN_INITIAL_YEAR,
                    endDatePickerDialog.getDatePicker().getMonth(),
                    endDatePickerDialog.getDatePicker().getDayOfMonth(),0,0,0);


            if(exerciseList.size() != 0){
                setRepData(exerciseList.get(exerciseSpinner.getSelectedIndex()).getExerciseId(), startDate, endDate);
            }

        });


    }


    private void setBarChartConfig(){

        chart.getDescription().setEnabled(false);
        chart.setNoDataTextColor(Color.BLACK);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.setExtraOffsets(8, 8, 8, 24);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.animateY(1500);
        chart.getLegend().setEnabled(true);
        chart.getLegend().setYEntrySpace(100.0f);
        chart.getLegend().setXEntrySpace(100.0f);
        chart.getLegend().
                setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        xAxis = chart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setAxisLineColor(Color.TRANSPARENT);

        xAxis.setLabelRotationAngle(315.0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
    }

    private void setRepData(int exerciseId, Date startDate, Date endDate) {

        chart.clear();
        values = new ArrayList<>();
        if(chart.getData() != null){
            chart.getData().clearValues();

        }

        chart.setNoDataText("No chart data available");


        AppExecutors.getInstance().diskIO().execute(() -> {

            List<TotalVolume> allByVolume = WorkoutDatabase.getInstance(getApplicationContext())
                    .repInfoDao()
                    .getAllBetweenStartDateAndEndDate(exerciseId, startDate, endDate);


            if(allByVolume.size() == 0){
                runOnUiThread(() -> {

                    chart.clear();
                    chart.setNoDataText("No chart data available");

                    if(chart.getData() != null){
                        chart.getData().clearValues();

                    }

                });

                return;
            }


            String pattern = "dd-MMM";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            for (int i = 0; i < allByVolume.size(); i++) {
                String date = simpleDateFormat.format(allByVolume.get(i).getDate());
                values.add(new BarEntry(i, allByVolume.get(i).getTotalVolume(), date));
            }

            xAxis.setLabelCount(values.size());

            BarDataSet barDataSet;


            if (chart.getData() != null &&
                    chart.getData().getDataSetCount() > 0) {
                barDataSet = (BarDataSet) chart.getData().getDataSetByIndex(0);
                barDataSet.setValues(values);
                chart.getData().notifyDataChanged();
                chart.notifyDataSetChanged();
            } else {
                barDataSet = new BarDataSet(values, "Volume");
                barDataSet.setColors(ColorTemplate.rgb("#000"));
                barDataSet.setDrawValues(true);
                barDataSet.setValueTextSize(10.0f);
                barDataSet.setLabel("Volume (Kg)");


                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(barDataSet);

                BarData data = new BarData(dataSets);
                chart.setData(data);
                //chart.setFitBars(true);
            }

            List<String> dates = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                dates.add((String) values.get(i).getData());
            }

            xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));

            chart.invalidate();

        });

        AppExecutors.getInstance().diskIO().execute(() -> {

            RepInfo maxWeightLifted = WorkoutDatabase.getInstance(getApplicationContext())
                    .repInfoDao()
                    .getMaxWeightLifted(exerciseId, startDate, endDate);

            Float totalVolume = WorkoutDatabase.getInstance(getApplicationContext())
                    .repInfoDao()
                    .getTotalVolumeFromDateToDate(exerciseId, startDate, endDate);

            int exerciseCount = WorkoutDatabase.getInstance(getApplicationContext())
                    .repInfoDao()
                    .getExerciseFromDateToDate(exerciseId, startDate, endDate).size();


            if(maxWeightLifted != null && exerciseCount != 0)
            {
                runOnUiThread(() -> {
                    maxWeightTextView.setText("" + maxWeightLifted.getWeight() + " Kg × " + maxWeightLifted.getRep() + " Reps on " + maxWeightLifted.getDate().getDate() + "-" + (maxWeightLifted.getDate().getMonth() + 1) + "-" + (maxWeightLifted.getDate().getYear()+1900));
                    totalVolumeTextView.setText("" + totalVolume + " Kg");
                    exerciseCountTextView.setText("" + exerciseCount + " Day(s)");
                });
            }
            else{
                runOnUiThread(() -> {
                    maxWeightTextView.setText("No data available");
                    totalVolumeTextView.setText("No data available");
                    exerciseCountTextView.setText("No data available");
                });
            }

        });




    }


}