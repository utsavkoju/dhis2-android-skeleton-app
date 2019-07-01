package com.example.android.androidskeletonapp.ui.tracked_entity_instances.search;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.ListActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.TrackedEntityInstanceAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class TrackedEntityInstanceSearchActivity extends ListActivity {

    private ProgressBar progressBar;
    private TextView downloadDataText;
    private TextView notificator;
    private TrackedEntityInstanceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_tracked_entity_instance_search, R.id.trackedEntityInstancesToolbar,
                R.id.trackedEntityInstanceRecyclerView);

        notificator = findViewById(R.id.dataNotificator);
        downloadDataText = findViewById(R.id.downloadDataText);
        progressBar = findViewById(R.id.trackedEntityInstanceProgressBar);
        FloatingActionButton downloadButton = findViewById(R.id.downloadDataButton);

        adapter = new TrackedEntityInstanceAdapter();

        downloadButton.setOnClickListener(view -> {
            view.setEnabled(Boolean.FALSE);
            view.setVisibility(View.GONE);
            downloadDataText.setVisibility(View.GONE);
            Snackbar.make(view, "Searching data...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            notificator.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            searchTrackedEntityInstances();
        });
    }

    private void searchTrackedEntityInstances() {
        // TODO Search TEIs
    }
}
