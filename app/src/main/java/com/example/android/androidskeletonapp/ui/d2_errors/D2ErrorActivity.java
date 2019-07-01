package com.example.android.androidskeletonapp.ui.d2_errors;

import android.os.Bundle;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.ui.base.ListActivity;

public class D2ErrorActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_d2_errors, R.id.d2ErrorsToolbar, R.id.d2ErrorsRecyclerView);
        // TODO List D2 errors
    }
}
