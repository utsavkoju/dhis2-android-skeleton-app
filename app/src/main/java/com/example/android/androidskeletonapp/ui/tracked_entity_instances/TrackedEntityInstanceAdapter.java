package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithSyncHolder;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

public class TrackedEntityInstanceAdapter extends PagedListAdapter<TrackedEntityInstance, ListItemWithSyncHolder> {

    public TrackedEntityInstanceAdapter() {
        super(new DiffByIdItemCallback<>());

    }

    @NonNull
    @Override
    public ListItemWithSyncHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListItemWithSyncHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemWithSyncHolder holder, int position) {
        // TODO Bind Tei
    }
}
