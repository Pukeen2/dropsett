package com.dropsett.app.util;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

public class EmptyStateHelper {

    public static void observe(RecyclerView recycler, TextView emptyView, int itemCount) {
        if (itemCount == 0) {
            recycler.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}