package com.dropsett.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.model.ExerciseSet;

import java.util.List;

public class ExerciseHistoryAdapter
        extends RecyclerView.Adapter<ExerciseHistoryAdapter.ViewHolder> {

    private final List<ExerciseSet> sets;

    public ExerciseHistoryAdapter(List<ExerciseSet> sets) {
        this.sets = sets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_history_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseSet set = sets.get(position);
        holder.tvSetIndex.setText("Set " + set.setIndex);
        holder.tvWeight.setText(set.actualWeight > 0
                ? set.actualWeight + " kg" : "— kg");
        holder.tvReps.setText(set.actualReps > 0
                ? set.actualReps + " reps" : "— reps");
        holder.tvRpe.setText(set.rpe > 0
                ? "RPE " + set.rpe : "");
        holder.tvFailure.setText(set.toFailure ? "To failure" : "");
    }

    @Override
    public int getItemCount() {
        return sets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSetIndex, tvWeight, tvReps, tvRpe, tvFailure;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSetIndex = itemView.findViewById(R.id.tvHistorySetIndex);
            tvWeight = itemView.findViewById(R.id.tvHistoryWeight);
            tvReps = itemView.findViewById(R.id.tvHistoryReps);
            tvRpe = itemView.findViewById(R.id.tvHistoryRpe);
            tvFailure = itemView.findViewById(R.id.tvHistoryFailure);
        }
    }
}