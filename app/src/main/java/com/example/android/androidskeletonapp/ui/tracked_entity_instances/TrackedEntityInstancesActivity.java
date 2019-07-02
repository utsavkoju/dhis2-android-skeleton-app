package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static android.text.TextUtils.isEmpty;

public class TrackedEntityInstancesActivity extends ListActivity {

    private CompositeDisposable compositeDisposable;
    private String selectedProgram;

    private enum IntentExtra {
        PROGRAM
    }

    public static Intent getTrackedEntityInstancesActivityIntent(Context context, String program) {
        Intent intent = new Intent(context, TrackedEntityInstancesActivity.class);
        intent.putExtra(IntentExtra.PROGRAM.name(), program);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_tracked_entity_instances, R.id.trackedEntityInstancesToolbar,
                R.id.trackedEntityInstancesRecyclerView);
        selectedProgram = getIntent().getStringExtra(IntentExtra.PROGRAM.name());
        compositeDisposable = new CompositeDisposable();

        // TODO Uncomment this method to trigger trackedEntitySearch
        // observeTrackedEntityInstances();
    }

    private void observeTrackedEntityInstances() {
        TrackedEntityInstanceAdapter adapter = new TrackedEntityInstanceAdapter();
        recyclerView.setAdapter(adapter);

        getTeiRepository().getPaged(20).observe(this, trackedEntityInstancePagedList -> {
            adapter.submitList(trackedEntityInstancePagedList);
            findViewById(R.id.trackedEntityInstancesNotificator).setVisibility(
                    trackedEntityInstancePagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private TrackedEntityInstanceCollectionRepository getTeiRepository() {
        // TODO Get TrackedEntityInstanceRepository to show trackedEntityInstances with Attributes
        TrackedEntityInstanceCollectionRepository teiRepository = null;

        if (!isEmpty(selectedProgram)) {
            // TODO If program is selected, filter by program uid
            return teiRepository;
        } else {
            return teiRepository;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }
}
