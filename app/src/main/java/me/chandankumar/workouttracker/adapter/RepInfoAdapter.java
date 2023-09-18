package me.chandankumar.workouttracker.adapter;

import android.app.Activity;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;
import java.util.List;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.database.AppExecutors;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.domain.RepInfo;
import me.chandankumar.workouttracker.ui.RepInfoActivity;
import me.chandankumar.workouttracker.utils.AlertDialogBuilder;
import me.chandankumar.workouttracker.utils.Constants;

public class RepInfoAdapter extends RecyclerView.Adapter<RepInfoAdapter.ViewHolder> {

    private Activity activity;
    private List<RepInfo> repInfoList;
    private WorkoutDatabase workoutDatabase;

    public RepInfoAdapter(Activity activity, List<RepInfo> repInfoList, WorkoutDatabase workoutDatabase) {
        this.activity = activity;
        this.repInfoList = repInfoList;
        this.workoutDatabase = workoutDatabase;
    }

    public void refresh(List<RepInfo> reps){
        repInfoList.clear();
        repInfoList.addAll(reps);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rep_item, parent, false);
        return new RepInfoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.repInfoTextView.setText("" + repInfoList.get(position).getWeight() + " Kg × " + repInfoList.get(position).getRep());

        holder.deleteImageView.setOnClickListener(view -> {

            AlertDialogBuilder.getConfirmAlertDialog(activity, "Delete", "Are you sure you want to delete this log?")
                            .setPositiveButton(Constants.YES, (dialogInterface, i) ->
                                    AppExecutors.getInstance().diskIO().execute(() -> {
                                        workoutDatabase.repInfoDao().delete(repInfoList.get(position));
                                        List<RepInfo> repInfos = workoutDatabase.repInfoDao().getAllByDateAndExerciseId(repInfoList.get(position).getDate(), repInfoList.get(position).getExerciseId());
                                        activity.runOnUiThread(() -> refresh(repInfos));

                                    }))
                            .setNegativeButton(Constants.NO, (dialogInterface, i) -> dialogInterface.dismiss()).show();
        });


        holder.editImageView.setOnClickListener(view -> {

            View viewInflated = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.rep_info_input_dialog, null , false);

            final EditText weightEditText = viewInflated.findViewById(R.id.weight);
            final EditText repsEditText = viewInflated.findViewById(R.id.reps);

            weightEditText.setText(""+repInfoList.get(position).getWeight());
            repsEditText.setText(""+repInfoList.get(position).getRep());

            AlertDialog alertDialog = new MaterialAlertDialogBuilder(activity)
                    .setTitle("Update Exercise")
                    .setView(viewInflated)
                    .setPositiveButton(Constants.YES, null)
                    .setNegativeButton(Constants.NO, (dialogInterface, i) -> {
                    }).show();


            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view1 -> {

                RepInfo repInfo = repInfoList.get(position);
                String weight = weightEditText.getText().toString().trim();
                String reps = repsEditText.getText().toString().trim();

                if(weight.isEmpty()){
                    weightEditText.setError("Weight cannot be empty");
                    return;
                }

                if(reps.isEmpty()){
                    repsEditText.setError("Reps cannot be empty");
                    return;
                }

                repInfo.setRep(Integer.parseInt(reps));
                repInfo.setWeight(Float.parseFloat(weight));

                AppExecutors.getInstance().diskIO().execute(() -> {
                    workoutDatabase.repInfoDao().update(repInfo);
                    List<RepInfo> repInfos = workoutDatabase.repInfoDao().getAllByDateAndExerciseId(repInfoList.get(position).getDate(), repInfoList.get(position).getExerciseId());
                    activity.runOnUiThread(() -> refresh(repInfos));
                });

                alertDialog.dismiss();

            });

        });

    }

    @Override
    public int getItemCount() {
        return repInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView repInfoTextView;
        ImageView editImageView;
        ImageView deleteImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            repInfoTextView = itemView.findViewById(R.id.rep_info_textview);
            editImageView = itemView.findViewById(R.id.edit_imageview);
            deleteImageView = itemView.findViewById(R.id.delete_imageview);

        }
    }

}
