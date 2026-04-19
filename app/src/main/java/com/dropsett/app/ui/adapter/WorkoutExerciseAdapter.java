package com.dropsett.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.dropsett.app.util.InputValidator;

import java.util.ArrayList;
import java.util.List;

public class WorkoutExerciseAdapter
        extends RecyclerView.Adapter<WorkoutExerciseAdapter.ExerciseViewHolder> {

    public static class WorkoutExerciseItem {
        public Exercise exercise;
        public List<ExerciseSet> sets = new ArrayList<>();
        public List<ExerciseSet> hints = new ArrayList<>(); // parallel hint list

        public WorkoutExerciseItem(Exercise exercise) {
            this.exercise = exercise;
        }
    }

    private final List<WorkoutExerciseItem> items = new ArrayList<>();
    private final AppDatabase db;

    public WorkoutExerciseAdapter(AppDatabase db) {
        this.db = db;
    }

    public void addExercise(Exercise exercise, int initialSets, int targetRpe) {
        WorkoutExerciseItem item = new WorkoutExerciseItem(exercise);
        for (int i = 0; i < initialSets; i++) {
            item.sets.add(new ExerciseSet(0, i + 1, 0, 0f, 0, 0f, targetRpe, false));
        }
        items.add(item);
        int insertedAt = items.size() - 1;

        // load hints once — store on item, not rebuilt every bind
        AppExecutors.diskIO().execute(() -> {
            SessionExercise lastSE = db.sessionDao()
                    .getLastSessionExercise(exercise.id);
            List<ExerciseSet> hints = new ArrayList<>();
            if (lastSE != null) {
                hints = db.sessionDao().getSetsForSessionExercise(lastSE.id);
            }
            final List<ExerciseSet> finalHints = hints;
            // attach hints to set objects
            for (int i = 0; i < item.sets.size(); i++) {
                if (i < finalHints.size()) {
                    item.sets.get(i).hintWeight = finalHints.get(i).actualWeight;
                    item.sets.get(i).hintReps   = finalHints.get(i).actualReps;
                }
            }
            item.hints = finalHints;
            notifyItemChanged(insertedAt);
        });
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

        buildSetRows(holder, item);

        holder.btnAddSet.setOnClickListener(v -> {
            int nextIndex = item.sets.size() + 1;
            ExerciseSet newSet = new ExerciseSet(0, nextIndex, 0, 0f, 0, 0f, 0, false);
            // apply hint for this index if available
            if (item.hints.size() >= nextIndex) {
                ExerciseSet hint = item.hints.get(nextIndex - 1);
                newSet.hintWeight = hint.actualWeight;
                newSet.hintReps   = hint.actualReps;
            }
            item.sets.add(newSet);
            // rebuild only the sets container, not the whole item
            buildSetRows(holder, item);
        });

        holder.btnRemoveExercise.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID) {
                items.remove(pos);
                notifyItemRemoved(pos);
            }
        });
    }

    private void buildSetRows(ExerciseViewHolder holder, WorkoutExerciseItem item) {
        holder.setsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());

        for (int i = 0; i < item.sets.size(); i++) {
            ExerciseSet set = item.sets.get(i);
            final int setIndex = i;

            View setRow = inflater.inflate(
                    R.layout.item_set_row, holder.setsContainer, false);

            TextView tvSetNum  = setRow.findViewById(R.id.tvSetNumber);
            EditText etWeight  = setRow.findViewById(R.id.etWeight);
            EditText etReps    = setRow.findViewById(R.id.etReps);
            EditText etRpe     = setRow.findViewById(R.id.etRpe);
            CheckBox cbFailure = setRow.findViewById(R.id.cbFailure);
            TextView btnRemoveSet = setRow.findViewById(R.id.btnRemoveSet);

            tvSetNum.setText(String.valueOf(i + 1));

            // restore already-entered values (prevents clearing on add set)
            if (set.actualWeight > 0)
                etWeight.setText(String.valueOf(set.actualWeight));
            if (set.actualReps > 0)
                etReps.setText(String.valueOf(set.actualReps));
            if (set.rpe > 0)
                etRpe.setText(String.valueOf(set.rpe));
            cbFailure.setChecked(set.toFailure);

            // show hints only if no value entered yet
            if (set.actualWeight == 0 && set.hintWeight > 0)
                etWeight.setHint(String.valueOf(set.hintWeight));
            if (set.actualReps == 0 && set.hintReps > 0)
                etReps.setHint(String.valueOf(set.hintReps));

            etWeight.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String val = etWeight.getText().toString().trim();
                    if (!InputValidator.isValidWeight(val)) {
                        etWeight.setError("Invalid");
                        return;
                    }
                    set.actualWeight = val.isEmpty() ? 0f : Float.parseFloat(val);
                }
            });

            etReps.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String val = etReps.getText().toString().trim();
                    if (!InputValidator.isValidReps(val)) {
                        etReps.setError("Invalid");
                        return;
                    }
                    set.actualReps = val.isEmpty() ? 0 : Integer.parseInt(val);
                }
            });

            etRpe.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String val = etRpe.getText().toString().trim();
                    if (!InputValidator.isValidRpe(val)) {
                        etRpe.setError("1–10");
                        return;
                    }
                    set.rpe = val.isEmpty() ? 0 : Integer.parseInt(val);
                }
            });

            cbFailure.setOnCheckedChangeListener((btn, checked) -> {
                set.toFailure = checked;
                // when failure is checked, grey out RPE to signal it takes priority
                etRpe.setEnabled(!checked);
                etRpe.setAlpha(checked ? 0.4f : 1f);
            });

            // apply initial failure state
            etRpe.setEnabled(!set.toFailure);
            etRpe.setAlpha(set.toFailure ? 0.4f : 1f);

            btnRemoveSet.setOnClickListener(v -> {
                item.sets.remove(setIndex);
                // re-index remaining sets
                for (int j = 0; j < item.sets.size(); j++) {
                    item.sets.get(j).setIndex = j + 1;
                }
                buildSetRows(holder, item);
            });

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
        TextView btnAddSet, btnRemoveExercise;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName   = itemView.findViewById(R.id.tvExerciseName);
            setsContainer    = itemView.findViewById(R.id.setsContainer);
            btnAddSet        = itemView.findViewById(R.id.btnAddSet);
            btnRemoveExercise = itemView.findViewById(R.id.btnRemoveExercise);
        }
    }
}