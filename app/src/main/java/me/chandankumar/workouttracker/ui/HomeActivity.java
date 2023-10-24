package me.chandankumar.workouttracker.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skydoves.powerspinner.PowerSpinnerView;

import me.chandankumar.workouttracker.R;

public class HomeActivity extends AppCompatActivity {

    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private EditText weightEditText;
    private EditText repsEditText;
    private PowerSpinnerView unitSpinner;
    private TextView oneRMTextView;
    private NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        nestedScrollView = findViewById(R.id.activity_home_ui);
        nestedScrollView.setBackground(getResources().getDrawable(R.drawable.background_img_blue));
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
        setupBottomSheetView();

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

    private void setupBottomSheetView(){
        bottomSheetDialog = new BottomSheetDialog(
                HomeActivity.this, R.style.BottomSheetDialogTheme);

        bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.one_rep_max_calc_bottom_sheet,
                        (LinearLayout)findViewById(R.id.bottom_sheet_container));

        weightEditText = bottomSheetView.findViewById(R.id.weight_edittext);
        repsEditText = bottomSheetView.findViewById(R.id.reps_edittext);
        unitSpinner = bottomSheetView.findViewById(R.id.weight_unit_spinner);
        oneRMTextView = bottomSheetView.findViewById(R.id.one_rep_max_result);
        unitSpinner.selectItemByIndex(0);

        bottomSheetView.findViewById(R.id.show_button).setOnClickListener(view1 -> {



            float weight = Float.parseFloat(weightEditText.getText().toString());
            int reps = Integer.parseInt(repsEditText.getText().toString());

            double oneRMResult = weight / (1.0278 - (0.0278 * reps));
            oneRMResult = ((double) Math.round(oneRMResult * 100.0) / 100.0);

            oneRMTextView.setText("Your 1RM is " + oneRMResult + " " + unitSpinner.getText());


        });

        bottomSheetDialog.setContentView(bottomSheetView);
    }

    public void showOneRepMaxCalcActivity(View view) {
        bottomSheetDialog.show();
    }

    public void showSettingActivity(View view) {
        startActivity(new Intent(this, SettingActivity.class));
    }

}