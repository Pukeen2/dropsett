package com.dropsett.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.model.PlanDay;

import java.util.List;

public class PlanDayAdapter extends RecyclerView.Adapter<PlanDayAdapter.ViewHolder> {

    public interface OnDayClickListener {
        void onDayClick(PlanDay day);
    }

    private final List<PlanDay> days;
    private final OnDayClickListener listener;

    public PlanDayAdapter(List<PlanDay> days, OnDayClickListener listener) {
        this.days = days;
        this.listener = listener;
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
        holder.tvDayName.setText("Day " + (day.dayIndex + 1));
        holder.tvDayLabel.setText(day.label);
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onDayClick(day));
        }
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