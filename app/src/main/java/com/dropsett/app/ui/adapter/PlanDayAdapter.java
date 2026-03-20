package com.dropsett.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.model.PlanDay;
import com.dropsett.app.util.DateUtil;

import java.util.List;

public class PlanDayAdapter extends RecyclerView.Adapter<PlanDayAdapter.ViewHolder> {

    private final List<PlanDay> days;

    public PlanDayAdapter(List<PlanDay> days) {
        this.days = days;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlanDay day = days.get(position);
        holder.tvDayName.setText(DateUtil.DAY_NAMES[day.dayOfWeek]);
        holder.tvDayLabel.setText(day.label);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDayLabel = itemView.findViewById(R.id.tvDayLabel);
        }
    }
}