package com.example.android.androidskeletonapp.ui.data_sets.reports;

import android.os.Bundle;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

public class DataSetReportsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_data_set_reports, R.id.dataSetReportsToolbar, R.id.dataSetReportsRecyclerView);
        // TODO list data set reports
    }
}
