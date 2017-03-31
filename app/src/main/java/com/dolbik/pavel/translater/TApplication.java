package com.dolbik.pavel.translater;

import android.app.Application;
import android.content.Context;

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

    public static RefWatcher getRefWatcher(Context context) {
        TApplication application = (TApplication) context.getApplicationContext();
        return application.refWatcher;
    }

}
