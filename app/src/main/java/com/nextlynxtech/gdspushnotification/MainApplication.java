package com.nextlynxtech.gdspushnotification;

import android.app.Application;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.nextlynxtech.gdspushnotification.api.APICalls;
import com.nextlynxtech.gdspushnotification.api.ApiService;

import retrofit.RestAdapter;

public class MainApplication extends Application {

    public static APICalls service;
    public static ApiService apiService;
    public static int lvIndex = 0, lvTop = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Consts.WEB_API).setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        service = restAdapter.create(APICalls.class);

        RestAdapter adapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Consts.WEB_API_URL)
                .build();
        apiService = adapter.create(ApiService.class);
        LocationLibrary.initialiseLibrary(getBaseContext(), "com.netlynxtech.gdsfileupload");
    }
}
