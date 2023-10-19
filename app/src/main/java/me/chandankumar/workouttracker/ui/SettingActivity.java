package me.chandankumar.workouttracker.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import me.chandankumar.workouttracker.R;

public class SettingActivity extends AppCompatActivity {

    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setupBottomSheetView();
    }

    public void showCreditsSheet(View view) {
        bottomSheetDialog.show();
    }

    private void setupBottomSheetView(){
        bottomSheetDialog = new BottomSheetDialog(
                SettingActivity.this, R.style.BottomSheetDialogTheme);

        bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.credits_bottom_sheet,
                        (LinearLayout)findViewById(R.id.bottom_sheet_container));


        bottomSheetDialog.setContentView(bottomSheetView);
    }
}