package com.dropsett.app.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.ui.adapter.SessionHistoryAdapter;
import com.dropsett.app.util.EmptyStateHelper;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        AppDatabase db = AppDatabase.getInstance(this);

        RecyclerView recycler = findViewById(R.id.recyclerHistory);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        SessionHistoryAdapter adapter = new SessionHistoryAdapter(db, session -> {
            SessionDetailActivity.start(this, session.id);
        });
        recycler.setAdapter(adapter);
        recycler.addItemDecoration(
                new androidx.recyclerview.widget.DividerItemDecoration(
                        this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                )
        );

        TextView tvEmpty = findViewById(R.id.tvEmptyHistory);

        db.sessionDao().getAllSessions().observe(this, sessions -> {
            adapter.setSessions(sessions);
            EmptyStateHelper.observe(recycler, tvEmpty, sessions.size());
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Workout History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}