package com.example.android.androidskeletonapp.ui.data_sets.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.DiffByIdItemCallback;
import com.example.android.androidskeletonapp.ui.base.ListItemWithSyncHolder;

import org.hisp.dhis.android.core.datavalue.DataSetReport;

import java.text.MessageFormat;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setBackgroundColor;
import static com.example.android.androidskeletonapp.data.service.StyleBinderHelper.setState;

public class DataSetReportsAdapter extends PagedListAdapter<DataSetReport, ListItemWithSyncHolder> {

    DataSetReportsAdapter() {
        super(new DiffByIdItemCallback<>());
    }

    @NonNull
    @Override
    public ListItemWithSyncHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ListItemWithSyncHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemWithSyncHolder holder, int position) {
        DataSetReport dataSetReport = getItem(position);

        // TODO Set meaningful texts to textViews
        holder.title.setText(dataSetReport.dataSetDisplayName());
        holder.subtitle1.setText(dataSetReport.organisationUnitDisplayName());
        holder.subtitle2.setText(dataSetReport.period());

        // TODO Set value count
        holder.rightText.setText(dataSetReport.valueCount().toString());

        holder.icon.setImageResource(R.drawable.ic_assignment_black_24dp);
        setBackgroundColor(R.color.colorAccentDark, holder.icon);
        setState(dataSetReport.state(), holder.syncIcon);
    }
}
