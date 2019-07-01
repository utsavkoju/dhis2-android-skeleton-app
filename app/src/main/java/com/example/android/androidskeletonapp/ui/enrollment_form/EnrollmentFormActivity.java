package com.example.android.androidskeletonapp.ui.enrollment_form;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.forms.EnrollmentFormService;
import com.example.android.androidskeletonapp.databinding.ActivityEnrollmentFormBinding;

import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class EnrollmentFormActivity extends AppCompatActivity {

    private ActivityEnrollmentFormBinding binding;
    private FormAdapter adapter;
    private CompositeDisposable disposable;

    private enum IntentExtra {
        TEI_UID, PROGRAM_UID, OU_UID
    }

    public static Intent getFormActivityIntent(Context context, String teiUid, String programUid,
                                               String orgUnitUid) {
        Intent intent = new Intent(context, EnrollmentFormActivity.class);
        intent.putExtra(IntentExtra.TEI_UID.name(), teiUid);
        intent.putExtra(IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(IntentExtra.OU_UID.name(), orgUnitUid);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enrollment_form);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        adapter = new FormAdapter((fieldUid, value) -> {
            try {
                Sdk.d2().trackedEntityModule().trackedEntityAttributeValues.value(fieldUid,
                        getIntent().getStringExtra(IntentExtra.TEI_UID.name()))
                        .set(value);
            } catch (D2Error d2Error) {
                d2Error.printStackTrace();
            }
        });
        binding.buttonEnd.setOnClickListener(this::finishEnrollment);
        binding.formRecycler.setAdapter(adapter);

        EnrollmentFormService.getInstance().init(
                Sdk.d2(),
                getIntent().getStringExtra(IntentExtra.TEI_UID.name()),
                getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name()),
                getIntent().getStringExtra(IntentExtra.OU_UID.name()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        disposable = new CompositeDisposable();

        disposable.add(
                EnrollmentFormService.getInstance().getEnrollmentFormFields()
                        .subscribeOn(Schedulers.io())
                        .map(fields -> new ArrayList<>(fields.values()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                fieldData -> adapter.updateData(fieldData),
                                Throwable::printStackTrace
                        )
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposable.clear();
    }

    @Override
    protected void onDestroy() {
        EnrollmentFormService.clear();
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void finishEnrollment(View view) {
        onBackPressed();
    }
}
