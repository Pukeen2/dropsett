package com.dropsett.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.model.Exercise;
import com.dropsett.app.model.ExerciseSet;

import java.util.List;

public class SessionDetailAdapter
        extends RecyclerView.Adapter<SessionDetailAdapter.ViewHolder> {

    public static class DetailItem {
        public Exercise exercise;
        public List<ExerciseSet> sets;

        public DetailItem(Exercise exercise, List<ExerciseSet> sets) {
            this.exercise = exercise;
            this.sets = sets;
        }
    }

    private final List<DetailItem> items;

    public SessionDetailAdapter(List<DetailItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session_detail_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailItem item = items.get(position);
        holder.tvExerciseName.setText(
                item.exercise != null ? item.exercise.name : "Unknown");
        holder.setsContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
        for (ExerciseSet set : item.sets) {
            View setRow = inflater.inflate(
                    R.layout.item_history_set_row, holder.setsContainer, false);

            TextView tvSet = setRow.findViewById(R.id.tvHistorySetNumber);
            TextView tvWeight = setRow.findViewById(R.id.tvHistoryWeight);
            TextView tvReps = setRow.findViewById(R.id.tvHistoryReps);
            TextView tvRpe = setRow.findViewById(R.id.tvHistoryRpe);
            TextView tvFailure = setRow.findViewById(R.id.tvHistoryFailure);

            tvSet.setText(String.valueOf(set.setIndex));
            tvWeight.setText(set.actualWeight > 0
                    ? set.actualWeight + "kg" : "-");
            tvReps.setText(set.actualReps > 0
                    ? String.valueOf(set.actualReps) : "-");
            tvRpe.setText(set.rpe > 0
                    ? "RPE " + set.rpe : "-");
            tvFailure.setText(set.toFailure ? "FAIL" : "");

            holder.setsContainer.addView(setRow);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName;
        LinearLayout setsContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
            setsContainer = itemView.findViewById(R.id.setsContainer);
        }
    }
}