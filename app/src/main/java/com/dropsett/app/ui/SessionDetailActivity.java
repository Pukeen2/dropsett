package com.dropsett.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.model.ExerciseSet;
import com.dropsett.app.model.SessionExercise;
import com.dropsett.app.model.WorkoutSession;
import com.dropsett.app.ui.adapter.SessionDetailAdapter;
import com.dropsett.app.util.AppExecutors;
import com.dropsett.app.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class SessionDetailActivity extends AppCompatActivity {

    private static final String EXTRA_SESSION_ID = "sessionId";

    public static void start(Context context, long sessionId) {
        Intent intent = new Intent(context, SessionDetailActivity.class);
        intent.putExtra(EXTRA_SESSION_ID, sessionId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        long sessionId = getIntent().getLongExtra(EXTRA_SESSION_ID, -1);
        AppDatabase db = AppDatabase.getInstance(this);

        TextView tvDate = findViewById(R.id.tvSessionDate);
        TextView tvDuration = findViewById(R.id.tvSessionDuration);
        RecyclerView recycler = findViewById(R.id.recyclerSessionDetail);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        AppExecutors.diskIO().execute(() -> {
            WorkoutSession session = db.sessionDao().getSessionById(sessionId);
            List<SessionExercise> sessionExercises =
                    db.sessionDao().getExercisesForSession(sessionId);

            List<SessionDetailAdapter.DetailItem> items = new ArrayList<>();
            for (SessionExercise se : sessionExercises) {
                com.dropsett.app.model.Exercise exercise =
                        db.exerciseDao().getById(se.exerciseId);
                List<ExerciseSet> sets =
                        db.sessionDao().getSetsForSessionExercise(se.id);
                items.add(new SessionDetailAdapter.DetailItem(exercise, sets));
            }

            runOnUiThread(() -> {
                tvDate.setText(DateUtil.formatDisplay(session.date));
                tvDuration.setText(formatDuration(session.durationSeconds));
                SessionDetailAdapter adapter = new SessionDetailAdapter(items);
                recycler.setAdapter(adapter);
            });
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Session Detail");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private String formatDuration(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        if (h > 0) return String.format("%d:%02d:%02d", h, m, s);
        return String.format("%02d:%02d", m, s);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}