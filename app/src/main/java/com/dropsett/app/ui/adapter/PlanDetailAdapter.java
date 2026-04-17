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
import com.dropsett.app.model.PlanDay;
import com.dropsett.app.model.PlanExercise;

import java.util.List;

public class PlanDetailAdapter
        extends RecyclerView.Adapter<PlanDetailAdapter.ViewHolder> {

    public static class DayDetailItem {
        public PlanDay day;
        public List<PlanExercise> planExercises;
        public List<Exercise> exercises;

        public DayDetailItem(PlanDay day,
                             List<PlanExercise> planExercises,
                             List<Exercise> exercises) {
            this.day = day;
            this.planExercises = planExercises;
            this.exercises = exercises;
        }
    }

    private final List<DayDetailItem> items;

    public PlanDetailAdapter(List<DayDetailItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan_day_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DayDetailItem item = items.get(position);
        holder.tvDayTitle.setText("Day " + (item.day.dayIndex + 1)
                + " — " + item.day.label);
        holder.exercisesContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
        for (int i = 0; i < item.planExercises.size(); i++) {
            PlanExercise pe = item.planExercises.get(i);
            Exercise ex = i < item.exercises.size() ? item.exercises.get(i) : null;

            View row = inflater.inflate(
                    R.layout.item_plan_detail_exercise_row,
                    holder.exercisesContainer, false);

            TextView tvName = row.findViewById(R.id.tvDetailExerciseName);
            TextView tvSets = row.findViewById(R.id.tvDetailSets);
            TextView tvRpe  = row.findViewById(R.id.tvDetailRpe);

            tvName.setText(ex != null ? ex.name : "Unknown");
            tvSets.setText(pe.targetSets + " sets");
            tvRpe.setText(pe.targetRpe > 0 ? "RPE " + pe.targetRpe : "");

            holder.exercisesContainer.addView(row);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayTitle;
        LinearLayout exercisesContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayTitle = itemView.findViewById(R.id.tvDayTitle);
            exercisesContainer = itemView.findViewById(R.id.exercisesContainer);
        }
    }
}