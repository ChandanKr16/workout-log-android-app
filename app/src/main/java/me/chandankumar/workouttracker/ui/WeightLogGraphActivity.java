package me.chandankumar.workouttracker.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.database.AppExecutors;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.domain.TotalVolume;
import me.chandankumar.workouttracker.domain.WeightLog;

public class WeightLogGraphActivity extends AppCompatActivity {

    private BarChart barChart;
    private List<BarModel> barModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_log_graph);

        initViews();



        setWeightLogData();

        barChart.startAnimation();

    }

    private void initViews(){
        barChart = findViewById(R.id.weight_bar_chart);
    }

    private void setWeightLogData() {

        barModelList = new ArrayList<>();
        AppExecutors.getInstance().diskIO().execute(() ->{
            List<WeightLog> logs = WorkoutDatabase.getInstance(getApplicationContext())
                    .weightLogDao().getAll();



            String pattern = "dd-MMM";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            for(int i = 0; i < logs.size(); i++){
                String date = simpleDateFormat.format(logs.get(i).getDate());
                barChart.addBar(new BarModel(date, logs.get(i).getWeight(), 0xFF1BA4E6));
            }
        });




    }
}