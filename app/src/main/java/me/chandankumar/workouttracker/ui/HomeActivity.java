package me.chandankumar.workouttracker.ui;

import static me.chandankumar.workouttracker.R.drawable.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skydoves.powerspinner.PowerSpinnerView;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.utils.SharedPref;
import me.chandankumar.workouttracker.utils.Theme;
import me.chandankumar.workouttracker.utils.ThemeColor;
import me.chandankumar.workouttracker.utils.Utils;

public class HomeActivity extends AppCompatActivity {

    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private EditText weightEditText;
    private EditText repsEditText;
    private PowerSpinnerView unitSpinner;
    private TextView oneRMTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupTheme();
        setupBottomSheetView();

    }

    private void setupTheme(){
        int background = SharedPref.getBackground(this);

        if(background == ThemeColor.GREEN.ordinal()){
            Theme.changeBackground(getApplicationContext(), findViewById(R.id.activity_home_ui), this.getWindow(),
                    getResources().getDrawable(background_img), R.color.green);
        }
        if(background == ThemeColor.BLUE.ordinal()){
            Theme.changeBackground(getApplicationContext(), findViewById(R.id.activity_home_ui), this.getWindow(),
                    getResources().getDrawable(background_img_blue), R.color.blue);
        }
        if(background == ThemeColor.PINK.ordinal()){
            Theme.changeBackground(getApplicationContext(), findViewById(R.id.activity_home_ui), this.getWindow(),
                    getResources().getDrawable(background_img_pink), R.color.pink);
        }
        if(background == ThemeColor.DARK.ordinal()){
            Theme.changeBackground(getApplicationContext(), findViewById(R.id.activity_home_ui), this.getWindow(),
                    getResources().getDrawable(background_img_dark), R.color.dark);
        }
    }

    private void setupThemeForBottomSheet(){
        int background = SharedPref.getBackground(this);

        if(background == ThemeColor.GREEN.ordinal()){
            Theme.changeBackground(getApplicationContext(), bottomSheetView.findViewById(R.id.bottom_sheet_container), this.getWindow(),
                    getResources().getDrawable(background_img), R.color.green);
        }
        if(background == ThemeColor.BLUE.ordinal()){
            Theme.changeBackground(getApplicationContext(), bottomSheetView.findViewById(R.id.bottom_sheet_container), this.getWindow(),
                    getResources().getDrawable(background_img_blue), R.color.blue);
        }
        if(background == ThemeColor.PINK.ordinal()){
            Theme.changeBackground(getApplicationContext(), bottomSheetView.findViewById(R.id.bottom_sheet_container), this.getWindow(),
                    getResources().getDrawable(background_img_pink), R.color.pink);
        }
        if(background == ThemeColor.DARK.ordinal()){
            Theme.changeBackground(getApplicationContext(), bottomSheetView.findViewById(R.id.bottom_sheet_container), this.getWindow(),
                    getResources().getDrawable(background_img_dark), R.color.dark);
        }
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

        LinearLayout linearLayout = findViewById(R.id.bottom_sheet_container);

        bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.one_rep_max_calc_bottom_sheet,
                       linearLayout);


        setupThemeForBottomSheet();

        weightEditText = bottomSheetView.findViewById(R.id.weight_edittext);
        repsEditText = bottomSheetView.findViewById(R.id.reps_edittext);
        unitSpinner = bottomSheetView.findViewById(R.id.weight_unit_spinner);
        oneRMTextView = bottomSheetView.findViewById(R.id.one_rep_max_result);
        unitSpinner.selectItemByIndex(0);

        bottomSheetView.findViewById(R.id.show_button).setOnClickListener(view1 -> {


            String weight = weightEditText.getText().toString()+"";
            String reps = repsEditText.getText().toString() + "";


            if(weight.isEmpty()){
                weightEditText.setError("Weight cannot be empty");
                return;
            }

            float weightVal = Float.parseFloat(weight);

            if(weightVal < 5 || weightVal > 999){
                weightEditText.setError("Invalid weight it should be between 5 to 999");
                return;
            }


            if(reps.isEmpty()){
                repsEditText.setError("Reps cannot be empty");
                return;
            }

            int repsVal = Integer.parseInt(reps);


            if(repsVal < 5 || repsVal > 30){
                repsEditText.setError("Invalid reps it should be between 5 to 31");
                return;
            }


            double oneRMResult = Utils.calculate1RM(repsVal, weightVal);

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