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
import me.chandankumar.workouttracker.domain.WeightLog;
import me.chandankumar.workouttracker.utils.AlertDialogBuilder;
import me.chandankumar.workouttracker.utils.Constants;

public class WeightLogAdapter extends RecyclerView.Adapter<WeightLogAdapter.ViewHolder> {

    private Activity activity;
    private List<WeightLog> weightLogList;
    private WorkoutDatabase workoutDatabase;

    public WeightLogAdapter(Activity activity, List<WeightLog> weightLogList, WorkoutDatabase workoutDatabase) {
        this.activity = activity;
        this.weightLogList = weightLogList;
        this.workoutDatabase = workoutDatabase;
    }

    public void refresh(List<WeightLog> logs){
        weightLogList.clear();
        weightLogList.addAll(logs);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weight_log_item, parent, false);
        return new WeightLogAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        float gain =  weightLogList.get(position).getGain();

        String date = weightLogList.get(position).getDate().getDate() + "-" + (weightLogList.get(position).getDate().getMonth()+1) + "-" + (weightLogList.get(position).getDate().getYear()+1900);

        holder.dateTextView.setText(date);
        holder.weightTextView.setText("" + weightLogList.get(position).getWeight() + " Kg");
        holder.gainTextView.setText("" + gain + " Kg");

        if(gain >= 0){
            holder.gainImageView.setImageResource(R.drawable.ic_baseline_arrow_upward_24);
        }
        else{
            holder.gainImageView.setImageResource(R.drawable.ic_baseline_arrow_downward_24);
        }




        holder.deleteImageView.setOnClickListener(view -> {

            AlertDialogBuilder.getConfirmAlertDialog(activity, "Delete", "Are you sure you want to delete this log?")
                            .setPositiveButton(Constants.YES, (dialogInterface, i) ->
                                    AppExecutors.getInstance().diskIO().execute(() -> {
                                        workoutDatabase.weightLogDao().delete(weightLogList.get(position));
                                        List<WeightLog> logs = workoutDatabase.weightLogDao().getAll();
                                        activity.runOnUiThread(() -> refresh(logs));

                                    }))
                            .setNegativeButton(Constants.NO, (dialogInterface, i) -> dialogInterface.dismiss()).show();
        });




    }

    @Override
    public int getItemCount() {
        return weightLogList.size();
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
