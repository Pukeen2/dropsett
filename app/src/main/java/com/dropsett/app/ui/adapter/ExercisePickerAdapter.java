package com.dropsett.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.model.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExercisePickerAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER   = 0;
    private static final int TYPE_EXERCISE = 1;

    public interface OnExercisePickedListener {
        void onPicked(Exercise exercise);
    }

    // flat list of items — either a header string or an Exercise
    private final List<Object> items = new ArrayList<>();
    private final OnExercisePickedListener listener;

    public ExercisePickerAdapter(OnExercisePickedListener listener) {
        this.listener = listener;
    }

    public void submitData(List<Exercise> recent, List<Exercise> all) {
        items.clear();
        if (!recent.isEmpty()) {
            items.add("Recently used");
            items.addAll(recent);
            items.add("All exercises");
        }
        items.addAll(all);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_EXERCISE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_picker_header, parent, false);
            return new HeaderViewHolder(view);
        }
        View view = inflater.inflate(R.layout.item_picker_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvHeader.setText((String) items.get(position));
        } else if (holder instanceof ExerciseViewHolder) {
            Exercise exercise = (Exercise) items.get(position);
            ExerciseViewHolder vh = (ExerciseViewHolder) holder;
            vh.tvName.setText(exercise.name);
            vh.tvMuscle.setText(exercise.muscleGroup
                    + (exercise.secondaryMuscle != null
                    && !exercise.secondaryMuscle.isEmpty()
                    ? " · " + exercise.secondaryMuscle : ""));
            vh.tvEquipment.setText(exercise.equipmentType);
            vh.tvType.setText(exercise.isCompound ? "Compound" : "Isolation");
            vh.itemView.setOnClickListener(v -> listener.onPicked(exercise));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvPickerHeader);
        }
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMuscle, tvEquipment, tvType;
        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName      = itemView.findViewById(R.id.tvPickerExerciseName);
            tvMuscle    = itemView.findViewById(R.id.tvPickerMuscle);
            tvEquipment = itemView.findViewById(R.id.tvPickerEquipment);
            tvType      = itemView.findViewById(R.id.tvPickerType);
        }
    }
}