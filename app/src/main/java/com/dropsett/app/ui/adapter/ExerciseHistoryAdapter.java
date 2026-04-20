package com.dropsett.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.model.ExerciseSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExerciseHistoryAdapter
        extends RecyclerView.Adapter<ExerciseHistoryAdapter.ViewHolder> {

    // Each item groups sets by session exercise id
    public static class SetGroup {
        public long sessionExerciseId;
        public String sessionLabel; // e.g. "27 Mar 2026"
        public List<ExerciseSet> sets;

        public SetGroup(long sessionExerciseId, String sessionLabel, List<ExerciseSet> sets) {
            this.sessionExerciseId = sessionExerciseId;
            this.sessionLabel      = sessionLabel;
            this.sets              = sets;
        }
    }

    private final List<SetGroup> groups;
    private final Set<Long> expandedIds = new HashSet<>();

    public ExerciseHistoryAdapter(List<SetGroup> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_history_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SetGroup group = groups.get(position);
        boolean expanded = expandedIds.contains(group.sessionExerciseId);

        holder.tvSessionLabel.setText(group.sessionLabel);
        holder.tvSetCount.setText(group.sets.size() + " sets");
        holder.setsContainer.setVisibility(expanded ? View.VISIBLE : View.GONE);
        holder.tvExpand.setText(expanded ? "▲" : "▼");

        holder.setsContainer.removeAllViews();
        if (expanded) {
            LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
            for (ExerciseSet set : group.sets) {
                View row = inflater.inflate(
                        R.layout.item_exercise_history_row,
                        holder.setsContainer, false);

                TextView tvIndex   = row.findViewById(R.id.tvHistorySetIndex);
                TextView tvWeight  = row.findViewById(R.id.tvHistoryWeight);
                TextView tvReps    = row.findViewById(R.id.tvHistoryReps);
                TextView tvRpe     = row.findViewById(R.id.tvHistoryRpe);
                TextView tvFailure = row.findViewById(R.id.tvHistoryFailure);

                tvIndex.setText("Set " + set.setIndex);
                tvWeight.setText(set.actualWeight > 0
                        ? set.actualWeight + " kg" : "— kg");
                tvReps.setText(set.actualReps > 0
                        ? set.actualReps + " reps" : "— reps");
                if (set.toFailure) {
                    tvRpe.setText("");
                    tvFailure.setText("FAIL");
                } else {
                    tvRpe.setText(set.rpe > 0 ? "RPE " + set.rpe : "");
                    tvFailure.setText("");
                }
                holder.setsContainer.addView(row);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (expanded) {
                expandedIds.remove(group.sessionExerciseId);
            } else {
                expandedIds.add(group.sessionExerciseId);
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSessionLabel, tvSetCount, tvExpand;
        LinearLayout setsContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSessionLabel = itemView.findViewById(R.id.tvExerciseHistorySession);
            tvSetCount     = itemView.findViewById(R.id.tvExerciseHistorySetCount);
            tvExpand       = itemView.findViewById(R.id.tvExerciseHistoryExpand);
            setsContainer  = itemView.findViewById(R.id.exerciseHistorySetsContainer);
        }
    }
}