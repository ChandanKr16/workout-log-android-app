package me.chandankumar.workouttracker.adapter;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.database.AppExecutors;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.domain.Exercise;
import me.chandankumar.workouttracker.domain.RepInfo;
import me.chandankumar.workouttracker.domain.TotalVolume;
import me.chandankumar.workouttracker.ui.RepInfoActivity;
import me.chandankumar.workouttracker.utils.AlertDialogBuilder;
import me.chandankumar.workouttracker.utils.Constants;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private Activity activity;
    private List<Exercise> exerciseList;
    private WorkoutDatabase workoutDatabase;
    private int bodyPartId;


    public ExerciseAdapter(Activity activity, List<Exercise> exerciseList, WorkoutDatabase workoutDatabase, int bodyPartId) {
        this.activity = activity;
        this.exerciseList = exerciseList;
        this.workoutDatabase = workoutDatabase;
        this.bodyPartId = bodyPartId;
    }


    public void refresh(List<Exercise> exercises){
        exerciseList.clear();
        exerciseList.addAll(exercises);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.exerciseNameTextView.setText(exerciseList.get(position).getExerciseName());
        holder.exerciseNameCardView.setOnClickListener(view -> {

            Intent intent = new Intent(activity, RepInfoActivity.class);
            intent.putExtra("exerciseId", exerciseList.get(position).getExerciseId());
            intent.putExtra("bodyPartId", bodyPartId);
            intent.putExtra("exerciseName", exerciseList.get(position).getExerciseName());
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        });

        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialogBuilder.getConfirmAlertDialog(activity, "Delete", "Are you sure you want to delete this exercise log?")
                        .setPositiveButton("YES", (dialogInterface, i) ->
                                AppExecutors.getInstance().diskIO().execute(() -> {
                                    workoutDatabase.exerciseDao().delete(exerciseList.get(position));
                                    List<Exercise> exercises = workoutDatabase.exerciseDao().getAllByBodyPartId(bodyPartId);

                                    activity.runOnUiThread(() -> {
                                        if(exercises.size() == 0){
                                            activity.findViewById(R.id.empty_img).setVisibility(View.VISIBLE);
                                            activity.findViewById(R.id.exercise_recyclerview).setVisibility(View.GONE);
                                        }
                                        refresh(exercises);
                                    });

                                }))
                        .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss()).show();
            }
        });

        holder.editImageView.setOnClickListener(view -> {

            View viewInflated = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.exercise_input_dialog, null , false);

            final EditText exerciseNameEditText = viewInflated.findViewById(R.id.exercise_name);

            exerciseNameEditText.setText(exerciseList.get(position).getExerciseName());


            AlertDialog alertDialog = new MaterialAlertDialogBuilder(activity)
                    .setTitle("Update Exercise Name")
                    .setView(viewInflated)
                    .setPositiveButton(Constants.YES, null)
                    .setNegativeButton(Constants.NO, (dialogInterface, i) -> {
                    }).show();


            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {

                Exercise exercise = exerciseList.get(position);

                String exerciseName = exerciseNameEditText.getText().toString().trim();

                if(exerciseName.isEmpty()){
                    exerciseNameEditText.setError("Exercise Name cannot be empty");
                    return;
                }

                exercise.setExerciseName(exerciseName);

                AppExecutors.getInstance().diskIO().execute(() -> {
                    workoutDatabase.exerciseDao().update(exercise);

                    List<Exercise> exercises = workoutDatabase.exerciseDao().getAllByBodyPartId(bodyPartId);

                    activity.runOnUiThread(() -> refresh(exercises));

                });

                alertDialog.dismiss();
            });
        });

        holder.previousRepInfoImageView.setOnClickListener(view -> {


            View viewInflated = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.last_rep_info_input_dialog, null , false);

            final TextView repInfoTextView = viewInflated.findViewById(R.id.rep_info_textview);
            final TextView totalVolumeTextView = viewInflated.findViewById(R.id.total_volume_info_textview);




            AlertDialog alertDialog = new MaterialAlertDialogBuilder(activity)
                    .setTitle("Last Rep Info")
                    .setView(viewInflated)
                    .setNeutralButton(Constants.OK, null)
                    .show();

            int exerciseId = exerciseList.get(position).getExerciseId();


            AppExecutors.getInstance().diskIO().execute(() -> {
                List<RepInfo> reps = workoutDatabase.repInfoDao().getLastPerformedExercise(exerciseId);
                TotalVolume totalVolume = workoutDatabase.repInfoDao().getTotalVolumeOfLastPerformedExercise(exerciseId);

                activity.runOnUiThread(() ->{

                    if(reps.isEmpty()) return;
                    alertDialog.setTitle("Last Rep Info - " + constructDate(reps.get(0).getDate()));
                    repInfoTextView.setText(constructRepInfoData(reps));
                    totalVolumeTextView.setText("Total Volume: " + totalVolume.getTotalVolume() + " Kg");

                });

            });



            Button neutralButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            neutralButton.setOnClickListener(v -> {
                alertDialog.dismiss();
            });
        });



    }

    private static String constructRepInfoData(List<RepInfo> reps){
        StringBuilder stringBuilder = new StringBuilder();
        for(RepInfo repInfo : reps){
            stringBuilder.append(repInfo.getRep());
            stringBuilder.append(" × ");
            stringBuilder.append(repInfo.getWeight());
            stringBuilder.append(" Kg\n");
        }

        return stringBuilder.toString();
    }

    private static String constructDate(Date date){
        return "" + date.getDate() + "-" + (date.getMonth()+1) + "-" + (date.getYear()+1900);
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseNameTextView;
        CardView exerciseNameCardView;
        ImageView editImageView;
        ImageView deleteImageView;
        ImageView previousRepInfoImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exercise_name_item);
            exerciseNameCardView = itemView.findViewById(R.id.exercise_name_card);
            editImageView = itemView.findViewById(R.id.edit_imageview);
            deleteImageView = itemView.findViewById(R.id.delete_imageview);
            previousRepInfoImageView = itemView.findViewById(R.id.last_rep_imageview);
        }
    }
}
