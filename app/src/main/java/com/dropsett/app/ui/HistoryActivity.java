package com.dropsett.app.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropsett.app.R;
import com.dropsett.app.data.AppDatabase;
import com.dropsett.app.ui.adapter.SessionHistoryAdapter;

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

        db.sessionDao().getAllSessions().observe(this, sessions -> {
            adapter.setSessions(sessions);
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