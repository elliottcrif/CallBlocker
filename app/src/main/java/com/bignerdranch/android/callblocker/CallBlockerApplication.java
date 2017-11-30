package com.bignerdranch.android.callblocker;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by elliottcrifasi on 11/30/17.
 */

public class CallBlockerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("callBlocker.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

    }
}
