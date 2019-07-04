package com.example.android.androidskeletonapp.ui.data_sets.reports;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.datavalue.DataSetReport;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.Date;

public class DataSetReportsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_data_set_reports, R.id.dataSetReportsToolbar, R.id.dataSetReportsRecyclerView);
        observeDataSetReports();
    }

    private void observeDataSetReports() {
        DataSetReportsAdapter adapter = new DataSetReportsAdapter();
        recyclerView.setAdapter(adapter);

        // TODO Get a LiveData for a PagedList from dataSetReports repository (dataValueModule)
        //  Pass this LiveData to the dataSetAdapter
        //  HINT: look at DataSetsActivity as a template

        LiveData<PagedList<DataSetReport>> liveData = Sdk.d2()
                .dataValueModule()
                .dataSetReports
                .getPaged(20);


        liveData.observe(this, drsList->{
            adapter.submitList(drsList);
            findViewById(R.id.dataSetReportsNotificator)
                    .setVisibility(drsList.isEmpty()?View.VISIBLE:View.GONE);
        });
    }
}
