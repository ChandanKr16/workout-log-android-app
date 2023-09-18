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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.database.AppExecutors;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.domain.Exercise;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exercise_name_item);
            exerciseNameCardView = itemView.findViewById(R.id.exercise_name_card);
            editImageView = itemView.findViewById(R.id.edit_imageview);
            deleteImageView = itemView.findViewById(R.id.delete_imageview);
        }
    }
}