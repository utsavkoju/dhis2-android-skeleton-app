package com.example.android.androidskeletonapp.ui.data_sets;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.dataset.DataSet;

import io.reactivex.disposables.Disposable;

public class DataSetsActivity extends ListActivity {

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_data_sets, R.id.dataSetsToolbar, R.id.dataSetsRecyclerView);
        // TODO List DataSets
        observeDataSet();
    }

    private void observeDataSet() {
        DataSetsAdapter adapter = new DataSetsAdapter();
        recyclerView.setAdapter(adapter);

        LiveData<PagedList<DataSet>> liveData = Sdk.d2().dataSetModule().dataSets.withStyle().getPaged(20);


        liveData.observe(this, d2ErrorPagedList -> {
            adapter.submitList(d2ErrorPagedList);
            findViewById(R.id.dataSetsNotificator).setVisibility(
                    d2ErrorPagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}
