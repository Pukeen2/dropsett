package com.dropsett.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.model.WorkoutPlan;

import java.util.ArrayList;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    public interface OnPlanClickListener {
        void onPlanClick(WorkoutPlan plan);
    }

    private List<WorkoutPlan> plans = new ArrayList<>();
    private final OnPlanClickListener listener;

    public PlanAdapter(OnPlanClickListener listener) {
        this.listener = listener;
    }

    public void setPlans(List<WorkoutPlan> plans) {
        this.plans = plans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutPlan plan = plans.get(position);
        holder.tvName.setText(plan.name);
        holder.tvDate.setText(plan.createdAt);
        holder.itemView.setOnClickListener(v -> listener.onPlanClick(plan));
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPlanName);
            tvDate = itemView.findViewById(R.id.tvPlanDate);
        }
    }
}