package me.chandankumar.workouttracker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.chandankumar.workouttracker.R;
import me.chandankumar.workouttracker.database.AppExecutors;
import me.chandankumar.workouttracker.database.WorkoutDatabase;
import me.chandankumar.workouttracker.domain.RepInfo;
import me.chandankumar.workouttracker.utils.AlertDialogBuilder;
import me.chandankumar.workouttracker.utils.Constants;

public class WeightLogAdapter extends RecyclerView.Adapter<WeightLogAdapter.ViewHolder> {

    private Activity activity;
    private List<RepInfo> repInfoList;
    private WorkoutDatabase workoutDatabase;

    public WeightLogAdapter(Activity activity, List<RepInfo> repInfoList, WorkoutDatabase workoutDatabase) {
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
        return new WeightLogAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.dateTextView.setText("" + repInfoList.get(position).getWeight() + " Kg × " + repInfoList.get(position).getRep());

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




    }

    @Override
    public int getItemCount() {
        return repInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView dateTextView;
        TextView weightTextView;
        TextView gainTextView;
        ImageView gainImageView;
        ImageView deleteImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.date_textview);
            weightTextView = itemView.findViewById(R.id.weight_textview);
            gainTextView = itemView.findViewById(R.id.gain_textview);
            gainImageView = itemView.findViewById(R.id.gain_imageview);
            deleteImageView = itemView.findViewById(R.id.delete_imageview);

        }
    }

}
