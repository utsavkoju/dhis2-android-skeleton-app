package com.example.android.androidskeletonapp.ui.foreign_key_violations;

import android.os.Bundle;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

public class ForeignKeyViolationsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_fk_violations, R.id.fkViolationsToolbar, R.id.fkViolationsRecyclerView);
        // TODO list foreign key violations
    }
}
