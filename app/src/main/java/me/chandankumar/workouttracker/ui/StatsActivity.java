package me.chandankumar.workouttracker.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.eazegraph.lib.models.BarModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.database.AppExecutors;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.domain.Exercise;
import me.chandankumar.workouttracker.domain.TotalVolume;
import me.chandankumar.workouttracker.ui.customviews.CustomEditText;
import me.chandankumar.workouttracker.ui.customviews.DrawableClickListener;

public class StatsActivity extends AppCompatActivity {

    private BarChart chart;
    ArrayList<BarEntry> values;
    private PowerSpinnerView muscleGroupSpinner;
    private PowerSpinnerView exerciseSpinner;
    private CustomEditText startDateEditText;
    private CustomEditText endDateEditText;
    private Button showButton;
    private List<Exercise> exerciseList;
    private XAxis xAxis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initViews();
        attachListenerOnDatePicker();



        // TODO load all exercise by body part then populate the exerise list from db to spinner
        //setRepData();

        setBarChartConfig();

//        loadData();
//        setData(10, 1000);
//        chart.invalidate();
    }

    private void initViews(){
        chart = findViewById(R.id.bar_chart);
        muscleGroupSpinner = findViewById(R.id.muscle_group_spinner);
        exerciseSpinner = findViewById(R.id.exercise_spinner);

        startDateEditText = (CustomEditText) findViewById(R.id.start_date_edit_text);
        endDateEditText = (CustomEditText) findViewById(R.id.end_date_edit_text);
        showButton = (Button) findViewById(R.id.show_button);

        muscleGroupSpinner.setOnSpinnerItemSelectedListener((i, o, bodyPartId, t1) -> {

            AppExecutors.getInstance().diskIO().execute(() ->{
                List<String> exerciseNameList = WorkoutDatabase.getInstance(getApplicationContext())
                        .exerciseDao()
                        .getAllExerciseNamesByBodyPartId(bodyPartId);


                exerciseList =  WorkoutDatabase.getInstance(getApplicationContext())
                        .exerciseDao()
                        .getAllByBodyPartId(bodyPartId);

                runOnUiThread(() -> exerciseSpinner.setItems(exerciseNameList));





            });


        });
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
                StatsActivity.this,
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
                StatsActivity.this,
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

            setRepData(exerciseSpinner.getSelectedIndex()+1);

        });


    }


    private void setBarChartConfig(){

        chart.getDescription().setEnabled(false);
        //chart.setMaxVisibleValueCount(10);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.setExtraOffsets(8, 8, 8, 8);
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
        xAxis.setLabelCount(10);
        xAxis.setLabelRotationAngle(315.0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);




    }

    private void setRepData(int exerciseId){

        chart.clear();

        values = new ArrayList<>();

        AppExecutors.getInstance().diskIO().execute(() ->{
            List<TotalVolume> allByVolume = WorkoutDatabase.getInstance(getApplicationContext())
                    .repInfoDao()
                    .getAllByVolume(exerciseId);

            String pattern = "dd-MMM";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            for(int i = 0; i < allByVolume.size(); i++){
                String date = simpleDateFormat.format(allByVolume.get(i).getDate());
                values.add(new BarEntry(i, allByVolume.get(i).getTotalVolume(), date));
            }


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
            for(int i = 0; i < values.size(); i++){
                dates.add( (String) values.get(i).getData());
            }

            xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));

            chart.invalidate();

        });



    }







    private void loadData(){
        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        chart.setPinchZoom(false);

       chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);


        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);

        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setLabelCount(10);
        xAxis.setLabelRotationAngle(315.0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);


        //chart.setPadding(8, 8, 8, 16);
        chart.setExtraOffsets(8, 8, 8, 8);




        chart.getAxisLeft().setDrawGridLines(false);

            List<String> dates = new ArrayList<>();


        for(int i = 0; i < 10; i++){
            dates.add("  15-feb");
            dates.add("  20-jan");
        }

        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));

        // add a nice and smooth animation
        chart.animateY(1500);

        chart.getLegend().setEnabled(true);
        chart.getLegend().setYEntrySpace(100.0f);
        chart.getLegend().setXEntrySpace(100.0f);
        chart.getLegend().
                setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

    }

    private void setData(int count, float range) {

        ArrayList<BarEntry> values = new ArrayList<>();



        for (int i = 0; i < count; i++) {
            float multi = (range + 1);
            float val = (float) (Math.random() * multi) + multi / 3;
            values.add(new BarEntry(i, val));
        }

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Data Set");
            set1.setColors(ColorTemplate.rgb("#000"));
            set1.setDrawValues(true);
            set1.setValueTextSize(10.0f);
            set1.setLabel("Volume");


            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            chart.setData(data);
           chart.setFitBars(true);
        }
    }
}