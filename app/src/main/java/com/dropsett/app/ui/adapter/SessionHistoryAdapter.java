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

    public interface OnSessionClickListener {
        void onSessionClick(WorkoutSession session);
    }
    public interface OnSessionDeleteListener {
        void onSessionDelete(WorkoutSession session);
    }

    private OnSessionDeleteListener deleteListener;

    public void setOnSessionDeleteListener(OnSessionDeleteListener listener) {
        this.deleteListener = listener;
    }

    private List<WorkoutSession> sessions = new ArrayList<>();
    private final com.dropsett.app.data.AppDatabase db;
    private final OnSessionClickListener listener;

    public SessionHistoryAdapter(com.dropsett.app.data.AppDatabase db,
                                 OnSessionClickListener listener) {
        this.db = db;
        this.listener = listener;
    }

    public void setSessions(List<WorkoutSession> sessions) {
        this.sessions = sessions;
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
        WorkoutSession session = sessions.get(position);
        holder.tvDate.setText(DateUtil.formatDisplay(session.date));
        holder.tvDuration.setText(formatDuration(session.durationSeconds));
        holder.itemView.setOnClickListener(v -> listener.onSessionClick(session));
        holder.itemView.setOnLongClickListener(v -> {
            if (deleteListener != null) deleteListener.onSessionDelete(session);
            return true;
        });
    }

    private String formatDuration(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        if (h > 0) return String.format("%d:%02d:%02d", h, m, s);
        return String.format("%02d:%02d", m, s);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvSessionDate);
            tvDuration = itemView.findViewById(R.id.tvSessionDuration);
        }
    }
}