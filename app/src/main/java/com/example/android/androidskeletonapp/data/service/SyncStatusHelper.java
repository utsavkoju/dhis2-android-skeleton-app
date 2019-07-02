package com.example.android.androidskeletonapp.data.service;

import com.example.android.androidskeletonapp.data.Sdk;

public class SyncStatusHelper {

    // TODO implement count methods

    public static int programCount() {
        return Sdk.d2().programModule().programs.count();
    }

    public static int dataSetCount() {
        return Sdk.d2().dataSetModule().dataSets.count();
    }

    public static int trackedEntityInstanceCount() {
        return Sdk.d2().trackedEntityModule().trackedEntityInstances.count();
    }

    public static int singleEventCount() {
        return Sdk.d2().eventModule().events.count();
    }

    public static int dataValueCount() {
        return Sdk.d2().dataValueModule().dataValues.count();
    }
}
