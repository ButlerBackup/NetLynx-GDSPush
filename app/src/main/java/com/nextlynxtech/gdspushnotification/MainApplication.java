package com.nextlynxtech.gdspushnotification;

import android.app.Application;

import com.nextlynxtech.gdspushnotification.api.APICalls;

import retrofit.RestAdapter;

public class MainApplication extends Application {

    public static APICalls service;

    @Override
    public void onCreate() {
        super.onCreate();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Consts.WEB_API).setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        service = restAdapter.create(APICalls.class);
    }
}
