package com.dolbik.pavel.translater;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dolbik.pavel.translater.db.DatabaseHelper;
import com.dolbik.pavel.translater.db.DatabaseManager;
import com.dolbik.pavel.translater.rest.RestApi;
import com.dolbik.pavel.translater.rest.RestService;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;


public class TApplication extends Application {

    private static TApplication instance;
    private RefWatcher refWatcher;


    public TApplication() { instance = this; }


    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) { return; }
        refWatcher = LeakCanary.install(this);
    }


    public static TApplication getInstance() { return instance; }


    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }


    public static RefWatcher getRefWatcher(Context context) {
        TApplication application = (TApplication) context.getApplicationContext();
        return application.refWatcher;
    }


    public RestApi getRestApi() {
        return RestService.getInstance().getRestApi(this);
    }


    public DatabaseHelper getHelper() {
        return DatabaseManager.getInstance().getHelper(this);
    }

}
