package com.example.android.androidskeletonapp.ui.d2_errors;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.maintenance.D2Error;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class D2ErrorActivity extends ListActivity {

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_d2_errors, R.id.d2ErrorsToolbar, R.id.d2ErrorsRecyclerView);
        // TODO List D2 errors
        observeError();
    }

    private void observeError() {
        D2ErrorAdapter adapter = new D2ErrorAdapter();
        recyclerView.setAdapter(adapter);
        LiveData<PagedList<D2Error>> errorPagedListLiveData = Sdk.d2().maintenanceModule().d2Errors.getPaged(15);
        errorPagedListLiveData.observe(this, errorsPagedList->{
            adapter.submitList(errorsPagedList);
            findViewById(R.id.d2ErrorsNotificator).setVisibility(
                    errorsPagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

}
