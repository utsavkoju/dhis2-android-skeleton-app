package com.example.android.androidskeletonapp.data.service;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.ui.login.LoginActivity;

import org.hisp.dhis.android.core.d2manager.D2Manager;

import io.reactivex.disposables.Disposable;

public class LogOutService {

    public static Disposable logOut(AppCompatActivity activity) {
        // TODO Logout and finish activity
        return Sdk.d2().userModule().logOut().subscribe(() -> {
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
            activity.finish();
        });
    }
}
