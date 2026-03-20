package com.dropsett.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.util.DateUtil;

public class PlanDayBuilderAdapter extends RecyclerView.Adapter<PlanDayBuilderAdapter.ViewHolder> {

    private final EditText[] labelInputs = new EditText[7];

    public String[] getDayLabels() {
        String[] labels = new String[7];
        for (int i = 0; i < 7; i++) {
            if (labelInputs[i] != null) {
                String text = labelInputs[i].getText().toString().trim();
                labels[i] = text.isEmpty() ? "Rest" : text;
            } else {
                labels[i] = "Rest";
            }
        }
        return labels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan_day_builder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvDayName.setText(DateUtil.DAY_NAMES[position]);
        labelInputs[position] = holder.etDayLabel;
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName;
        EditText etDayLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            etDayLabel = itemView.findViewById(R.id.etDayLabel);
        }
    }
}