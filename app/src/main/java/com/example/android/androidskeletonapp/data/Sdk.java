package com.example.android.androidskeletonapp.data;

import android.content.Context;

import org.hisp.dhis.android.core.d2manager.D2Configuration;

public class Sdk {

    static String app_name = "DHIS2 Capture";
    static String app_version = "0.0.1";
    static Integer time_out = 60;

    public static D2Configuration getD2Configuration(Context context) {
        D2Configuration config = D2Configuration.builder()
                .appName(app_name)
                .appVersion(app_version)
                .readTimeoutInSeconds(time_out).connectTimeoutInSeconds(time_out).writeTimeoutInSeconds(time_out)
                .context(context)
                .build();

        return config;
    }
}