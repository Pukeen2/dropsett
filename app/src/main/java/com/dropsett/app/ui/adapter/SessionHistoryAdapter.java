package com.dropsett.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.model.WorkoutSession;
import com.dropsett.app.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class SessionHistoryAdapter
        extends RecyclerView.Adapter<SessionHistoryAdapter.ViewHolder> {

    public static class SessionItem {
        public WorkoutSession session;
        public String label;

        public SessionItem(WorkoutSession session, String label) {
            this.session = session;
            this.label   = label;
        }
    }

    public interface OnSessionClickListener {
        void onSessionClick(WorkoutSession session);
    }

    public interface OnSessionDeleteListener {
        void onSessionDelete(WorkoutSession session);
    }

    private List<SessionItem> items = new ArrayList<>();
    private final OnSessionClickListener clickListener;
    private OnSessionDeleteListener deleteListener;

    public SessionHistoryAdapter(OnSessionClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnSessionDeleteListener(OnSessionDeleteListener l) {
        this.deleteListener = l;
    }

    public void setItems(List<SessionItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SessionItem item = items.get(position);
        holder.tvLabel.setText(item.label);
        holder.tvDate.setText(DateUtil.formatDisplay(item.session.date));
        holder.itemView.setOnClickListener(v -> clickListener.onSessionClick(item.session));
        holder.itemView.setOnLongClickListener(v -> {
            if (deleteListener != null) deleteListener.onSessionDelete(item.session);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvSessionLabel);
            tvDate  = itemView.findViewById(R.id.tvSessionDate);
        }
    }
}