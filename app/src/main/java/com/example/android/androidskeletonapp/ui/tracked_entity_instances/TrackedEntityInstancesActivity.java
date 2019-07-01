package com.example.android.androidskeletonapp.ui.tracked_entity_instances;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

import io.reactivex.disposables.CompositeDisposable;

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
        // TODO create TEI list
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }
}
