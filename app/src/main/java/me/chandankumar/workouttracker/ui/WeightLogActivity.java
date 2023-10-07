package me.chandankumar.workouttracker.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.adapter.WeightLogAdapter;
import me.chandankumar.workouttracker.database.AppExecutors;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.domain.Observer;
import me.chandankumar.workouttracker.domain.Subject;
import me.chandankumar.workouttracker.domain.WeightLog;
import me.chandankumar.workouttracker.utils.Constants;

public class WeightLogActivity extends AppCompatActivity implements Observer {

    private LottieAnimationView emptyImg;
    private RecyclerView weightLogRecyclerView;
    private WorkoutDatabase workoutDatabase;
    private WeightLogAdapter weightLogAdapter;
    private BarChart chart;
    private XAxis xAxis;
    private ArrayList<BarEntry> values;
    private Subject subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_log);

        workoutDatabase = WorkoutDatabase.getInstance(getApplicationContext());

        subject = new Subject();
        subject.registerObserver(this);

        initViews();
        weightLogRecyclerView.setVisibility(View.GONE);

        setBarChartConfig();
        setWeightLogData();
    }


    private void initViews(){
        weightLogRecyclerView = findViewById(R.id.weight_log_recyclerview);
        chart = findViewById(R.id.bar_chart);
        emptyImg = findViewById(R.id.empty_img);


        AppExecutors.getInstance().diskIO().execute(() -> {

            List<WeightLog> logs = workoutDatabase.weightLogDao().getAll();

            runOnUiThread(() -> {

                if(logs.size() == 0){
                    emptyImg.setVisibility(View.VISIBLE);
                    weightLogRecyclerView.setVisibility(View.GONE);

                }

                weightLogAdapter = new WeightLogAdapter(WeightLogActivity.this, logs, workoutDatabase, subject);
                weightLogRecyclerView.setHasFixedSize(true);
                weightLogRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                weightLogRecyclerView.setAdapter(weightLogAdapter);
            });

        });
    }


    public void showAddWeightLogDialog(View view1) {
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
                        runOnUiThread(() -> {
                            emptyImg.setVisibility(View.GONE);
                            weightLogRecyclerView.setVisibility(View.VISIBLE);
                            weightLogAdapter.refresh(logs);
                            setWeightLogData();
                        });

                        alertDialog.dismiss();
                });
                }});
        });
    }

    private void setBarChartConfig(){

        chart.getDescription().setEnabled(false);
        chart.setNoDataTextColor(Color.BLACK);
        chart.setTouchEnabled(true);
        chart.setNoDataText("No data available");
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

    public void  setWeightLogData(){
        chart.clear();
        values = new ArrayList<>();

        AppExecutors.getInstance().diskIO().execute(() -> {
            List<WeightLog> logs = WorkoutDatabase.getInstance(getApplicationContext())
                    .weightLogDao().getAll();

            AtomicBoolean isEmpty = new AtomicBoolean(false);

                runOnUiThread(() -> {
                    if(logs.size() == 0){
                        chart.setNoDataText("No data available");
                        emptyImg.setVisibility(View.VISIBLE);
                        weightLogRecyclerView.setVisibility(View.GONE);
                        isEmpty.set(true);

                    }
                    else{
                        weightLogRecyclerView.setVisibility(View.VISIBLE);
                        emptyImg.setVisibility(View.GONE);
                        isEmpty.set(false);
                    }

                    if(!isEmpty.get()) {


                        String pattern = "dd-MMM";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                        for (int i = 0; i < logs.size(); i++) {
                            String date = simpleDateFormat.format(logs.get(i).getDate());
                            values.add(new BarEntry(i, logs.get(i).getWeight(), date));
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
                            barDataSet = new BarDataSet(values, "Weight");
                            barDataSet.setColors(ColorTemplate.rgb("#000"));
                            barDataSet.setDrawValues(true);
                            barDataSet.setValueTextSize(10.0f);
                            barDataSet.setLabel("Volume (Kg)");


                            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                            dataSets.add(barDataSet);

                            BarData data = new BarData(dataSets);
                            chart.setData(data);
                        }

                        List<String> dates = new ArrayList<>();
                        for (int i = 0; i < values.size(); i++) {
                            dates.add((String) values.get(i).getData());
                        }

                        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));

                        chart.invalidate();
                    }

                });



        });
    }


    @Override
    public void update(int value) {
        setWeightLogData();
    }
}