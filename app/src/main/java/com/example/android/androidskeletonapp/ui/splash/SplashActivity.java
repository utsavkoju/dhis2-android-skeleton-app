package com.example.android.androidskeletonapp.ui.splash;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.d2manager.D2Configuration;
import org.hisp.dhis.android.core.d2manager.D2Manager;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

public class SplashActivity extends AppCompatActivity {

    private Disposable disposable;
    static String serverUrl = "http://android2.dhis2.org:8080";
    static String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        disposable =
                instantiateD2()
                        .doOnSuccess(d2 -> {
                                    Log.e(TAG,"Testing if the successful");
                                    Toast.makeText(getApplicationContext(),"Successful in D2 Configuration", Toast.LENGTH_LONG).show();
                                }
                        ).doOnError(throwable -> {
                    Log.e(TAG, "Failure");
                    Toast.makeText(getApplicationContext(),"Error in D2 Configuration", Toast.LENGTH_LONG).show();
                })
                        .subscribe();
        Log.e(TAG, "Outside");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private Single<D2> instantiateD2() {
        // TODO Instantiate d2
        D2Configuration config = Sdk.getD2Configuration(this);
        Single<D2> d2Single = D2Manager.setUp(config)
                .andThen(D2Manager.setServerUrl(serverUrl))
                .andThen(D2Manager.instantiateD2());
        return d2Single;
    }
}