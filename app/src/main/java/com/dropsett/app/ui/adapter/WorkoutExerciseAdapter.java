package com.dropsett.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.Exercise;
import com.dropsett.app.model.ExerciseSet;
import com.dropsett.app.model.SessionExercise;
import com.dropsett.app.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import com.dropsett.app.util.InputValidator;

public class WorkoutExerciseAdapter
        extends RecyclerView.Adapter<WorkoutExerciseAdapter.ExerciseViewHolder> {

    public static class WorkoutExerciseItem {
        public Exercise exercise;
        public List<ExerciseSet> sets = new ArrayList<>();

        public WorkoutExerciseItem(Exercise exercise) {
            this.exercise = exercise;
        }
    }

    private final List<WorkoutExerciseItem> items = new ArrayList<>();
    private final AppDatabase db;

    public WorkoutExerciseAdapter(AppDatabase db) {
        this.db = db;
    }

    public void addExercise(Exercise exercise) {
        WorkoutExerciseItem item = new WorkoutExerciseItem(exercise);
        // start with one empty set
        item.sets.add(new ExerciseSet(0, 1, 0, 0f, 0, 0f, 0, false));
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public List<WorkoutExerciseItem> getExerciseItems() {
        return items;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        WorkoutExerciseItem item = items.get(position);
        holder.tvExerciseName.setText(item.exercise.name);
        holder.setsContainer.removeAllViews();

        // load hints from last session in background
        AppExecutors.diskIO().execute(() -> {
            SessionExercise lastSE = db.sessionDao()
                    .getLastSessionExercise(item.exercise.id);
            List<ExerciseSet> lastSets = new ArrayList<>();
            if (lastSE != null) {
                lastSets = db.sessionDao().getSetsForSessionExercise(lastSE.id);
            }
            final List<ExerciseSet> hints = lastSets;

            holder.itemView.post(() -> {
                buildSetRows(holder, item, hints);
            });
        });

        holder.btnAddSet.setOnClickListener(v -> {
            int nextIndex = item.sets.size() + 1;
            item.sets.add(new ExerciseSet(0, nextIndex, 0, 0f, 0, 0f, 0, false));
            notifyItemChanged(holder.getAdapterPosition());
        });

        holder.btnRemoveExercise.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            items.remove(pos);
            notifyItemRemoved(pos);
        });
    }

    private void buildSetRows(ExerciseViewHolder holder,
                              WorkoutExerciseItem item,
                              List<ExerciseSet> hints) {
        holder.setsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());

        for (int i = 0; i < item.sets.size(); i++) {
            ExerciseSet set = item.sets.get(i);
            View setRow = inflater.inflate(R.layout.item_set_row, holder.setsContainer, false);

            TextView tvSetNum = setRow.findViewById(R.id.tvSetNumber);
            EditText etWeight = setRow.findViewById(R.id.etWeight);
            EditText etReps = setRow.findViewById(R.id.etReps);
            EditText etRpe = setRow.findViewById(R.id.etRpe);
            CheckBox cbFailure = setRow.findViewById(R.id.cbFailure);

            tvSetNum.setText(String.valueOf(i + 1));

            // apply hints from last time
            if (i < hints.size()) {
                ExerciseSet hint = hints.get(i);
                if (hint.actualWeight > 0)
                    etWeight.setHint(String.valueOf(hint.actualWeight));
                if (hint.actualReps > 0)
                    etReps.setHint(String.valueOf(hint.actualReps));
            }

            // bind to the set object on focus change
            final int setIndex = i;
            etWeight.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String val = etWeight.getText().toString().trim();
                    if (!InputValidator.isValidWeight(val)) {
                        etWeight.setError("Invalid weight");
                        return;
                    }
                    item.sets.get(setIndex).actualWeight =
                            val.isEmpty() ? 0f : Float.parseFloat(val);
                }
            });

            etReps.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String val = etReps.getText().toString().trim();
                    if (!InputValidator.isValidReps(val)) {
                        etReps.setError("Invalid reps");
                        return;
                    }
                    item.sets.get(setIndex).actualReps =
                            val.isEmpty() ? 0 : Integer.parseInt(val);
                }
            });

            etRpe.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String val = etRpe.getText().toString().trim();
                    if (!InputValidator.isValidRpe(val)) {
                        etRpe.setError("1–10 only");
                        return;
                    }
                    item.sets.get(setIndex).rpe =
                            val.isEmpty() ? 0 : Integer.parseInt(val);
                }
            });
            cbFailure.setOnCheckedChangeListener((btn, checked) ->
                    item.sets.get(setIndex).toFailure = checked);

            holder.setsContainer.addView(setRow);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName;
        LinearLayout setsContainer;
        Button btnAddSet, btnRemoveExercise;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
            setsContainer = itemView.findViewById(R.id.setsContainer);
            btnAddSet = itemView.findViewById(R.id.btnAddSet);
            btnRemoveExercise = itemView.findViewById(R.id.btnRemoveExercise);
        }
    }
}