package com.example.android.androidskeletonapp.ui.main;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.ActivityStarter;
import com.example.android.androidskeletonapp.data.service.SyncStatusHelper;
import com.example.android.androidskeletonapp.ui.d2_errors.D2ErrorActivity;
import com.example.android.androidskeletonapp.ui.data_sets.DataSetsActivity;
import com.example.android.androidskeletonapp.ui.foreign_key_violations.ForeignKeyViolationsActivity;
import com.example.android.androidskeletonapp.ui.programs.ProgramsActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.TrackedEntityInstancesActivity;
import com.example.android.androidskeletonapp.ui.tracked_entity_instances.search.TrackedEntityInstanceSearchActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.hisp.dhis.android.core.user.User;

import java.text.MessageFormat;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.android.androidskeletonapp.data.service.LogOutService.logOut;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CompositeDisposable compositeDisposable;

    private FloatingActionButton syncMetadataButton;
    private FloatingActionButton syncDataButton;
    private FloatingActionButton uploadDataButton;

    private TextView syncStatusText;
    private ProgressBar progressBar;

    private boolean isSyncing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        compositeDisposable = new CompositeDisposable();

        User user = getUser();
        TextView greeting = findViewById(R.id.greeting);
        greeting.setText(String.format("Hi %s!", user.displayName()));

        inflateMainView();
        createNavigationView(user);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSyncDataAndButtons();
    }

    private User getUser() {
        return Sdk.d2().userModule().user.getWithoutChildren();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    private void inflateMainView() {
        syncMetadataButton = findViewById(R.id.syncMetadataButton);
        syncDataButton = findViewById(R.id.syncDataButton);
        uploadDataButton = findViewById(R.id.uploadDataButton);

        syncStatusText = findViewById(R.id.notificator);
        progressBar = findViewById(R.id.syncProgressBar);

        syncMetadataButton.setOnClickListener(view -> {
            setSyncing();
            Snackbar.make(view, "Syncing metadata", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            syncStatusText.setText(R.string.syncing_metadata);
            syncMetadata();
        });

        syncDataButton.setOnClickListener(view -> {
            setSyncing();
            Snackbar.make(view, "Syncing data", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            syncStatusText.setText(R.string.syncing_data);
            downloadData();
        });

        uploadDataButton.setOnClickListener(view -> {
            setSyncing();
            Snackbar.make(view, "Uploading data", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            syncStatusText.setText(R.string.uploading_data);
            uploadData();
        });
    }

    private void setSyncing() {
        isSyncing = true;
        progressBar.setVisibility(View.VISIBLE);
        syncStatusText.setVisibility(View.VISIBLE);
        updateSyncDataAndButtons();
    }

    private void setSyncingFinished() {
        isSyncing = false;
        progressBar.setVisibility(View.GONE);
        syncStatusText.setVisibility(View.GONE);
        updateSyncDataAndButtons();
    }

    private void disableAllButtons() {
        setEnabledButton(syncMetadataButton, false);
        setEnabledButton(syncDataButton, false);
        setEnabledButton(uploadDataButton, false);
    }

    private void enablePossibleButtons(boolean metadataSynced) {
        if (!isSyncing) {
            setEnabledButton(syncMetadataButton, true);
            if (metadataSynced) {
                setEnabledButton(syncDataButton, true);
                if (SyncStatusHelper.isThereDataToUpload()) {
                    setEnabledButton(uploadDataButton, true);
                }
            }
        }
    }

    private void setEnabledButton(FloatingActionButton floatingActionButton, boolean enabled) {
        floatingActionButton.setEnabled(enabled);
        floatingActionButton.setAlpha(enabled ? 1.0f : 0.3f);
    }

    private void updateSyncDataAndButtons() {
        disableAllButtons();

        int programCount = SyncStatusHelper.programCount();
        int dataSetCount = SyncStatusHelper.dataSetCount();
        int trackedEntityInstanceCount = SyncStatusHelper.trackedEntityInstanceCount();
        int singleEventCount = SyncStatusHelper.singleEventCount();
        int dataValueCount = SyncStatusHelper.dataValueCount();

        enablePossibleButtons(programCount + dataSetCount > 0);

        TextView downloadedProgramsText = findViewById(R.id.programsDownloadedText);
        TextView downloadedDataSetsText = findViewById(R.id.dataSetsDownloadedText);
        TextView downloadedTeisText = findViewById(R.id.trackedEntityInstancesDownloadedText);
        TextView singleEventsDownloadedText = findViewById(R.id.singleEventsDownloadedText);
        TextView downloadedDataValuesText = findViewById(R.id.dataValuesDownloadedText);
        downloadedProgramsText.setText(MessageFormat.format("{0} Programs", programCount));
        downloadedDataSetsText.setText(MessageFormat.format("{0} Data sets", dataSetCount));
        downloadedTeisText.setText(MessageFormat.format("{0} Tracked entity instances", trackedEntityInstanceCount));
        singleEventsDownloadedText.setText(MessageFormat.format("{0} Events without registration", singleEventCount));
        downloadedDataValuesText.setText(MessageFormat.format("{0} Data values", dataValueCount));
    }

    private void createNavigationView(User user) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView firstName = headerView.findViewById(R.id.firstName);
        TextView email = headerView.findViewById(R.id.email);
        firstName.setText(user.firstName());
        email.setText(user.email());
    }

    private void syncMetadata() {
        compositeDisposable.add(Sdk.d2().syncMetaData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(Throwable::printStackTrace)
                .doOnComplete(() -> {
                    setSyncingFinished();
                    ActivityStarter.startActivity(this, ProgramsActivity.class, false);
                })
                .subscribe());
    }

    private void downloadData() {
        compositeDisposable.add(
                Observable.merge(
                        Sdk.d2().trackedEntityModule().downloadTrackedEntityInstances(10, false, false),
                        Sdk.d2().aggregatedModule().data().download()
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {
                            setSyncingFinished();
                            ActivityStarter.startActivity(this, TrackedEntityInstancesActivity.class, false);
                        })
                        .doOnError(Throwable::printStackTrace)
                        .subscribe());
    }

    private void uploadData() {
        compositeDisposable.add(
                Single.merge(
                        Single.fromCallable(Sdk.d2().trackedEntityModule().trackedEntityInstances.upload()),
                        Single.fromCallable(Sdk.d2().dataValueModule().dataValues.upload()),
                        Single.fromCallable(Sdk.d2().eventModule().events.upload())
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(this::setSyncingFinished)
                        .doOnError(Throwable::printStackTrace)
                        .subscribe());
    }

    private void wipeData() {
        compositeDisposable.add(
                Observable.fromCallable(() -> Sdk.d2().wipeModule().wipeData())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(Throwable::printStackTrace)
                        .doOnComplete(this::setSyncingFinished)
                        .subscribe());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        // TODO Launch data set activity

        if (id == R.id.navDataSets) {
            ActivityStarter.startActivity(this, DataSetsActivity.class, false);
        } else if (id == R.id.navPrograms) {
            ActivityStarter.startActivity(this, ProgramsActivity.class,false);
        } else if (id == R.id.navTrackedEntities) {
            ActivityStarter.startActivity(this, TrackedEntityInstancesActivity.class,false);
        } else if (id == R.id.navTrackedEntitiesSearch) {
            ActivityStarter.startActivity(this, TrackedEntityInstanceSearchActivity.class,false);
        } else if (id == R.id.navD2Errors) {
            ActivityStarter.startActivity(this, D2ErrorActivity.class,false);
        } else if (id == R.id.navFKViolations) {
            ActivityStarter.startActivity(this, ForeignKeyViolationsActivity.class,false);
        } else if (id == R.id.navWipeData) {
            syncStatusText.setText(R.string.wiping_data);
            wipeData();
        } else if (id == R.id.navExit) {
            compositeDisposable.add(logOut(this));
        }

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
