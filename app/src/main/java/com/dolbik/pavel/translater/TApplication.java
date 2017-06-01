package com.dolbik.pavel.translater;

import android.app.Application;
import android.content.Context;

import com.dolbik.pavel.translater.di.components.AppComponent;
import com.dolbik.pavel.translater.di.components.DaggerAppComponent;
import com.dolbik.pavel.translater.di.modules.BusModule;
import com.dolbik.pavel.translater.di.modules.ContextModule;
import com.dolbik.pavel.translater.di.modules.DbModule;
import com.dolbik.pavel.translater.di.modules.OnlineCheckerModule;
import com.dolbik.pavel.translater.di.modules.PrefModule;
import com.dolbik.pavel.translater.di.modules.RepositoryModule;
import com.dolbik.pavel.translater.di.modules.RestModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;


public class TApplication extends Application {

    private static TApplication instance;
    private static AppComponent appComponent;
    private RefWatcher refWatcher;


    public TApplication() { instance = this; }


    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) { return; }
        refWatcher = LeakCanary.install(this);

        appComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .prefModule(new PrefModule())
                .busModule(new BusModule())
                .onlineCheckerModule(new OnlineCheckerModule())
                .dbModule(new DbModule())
                .restModule(new RestModule())
                .repositoryModule(new RepositoryModule())
                .build();
    }


    public static AppComponent getAppComponent() {
        return appComponent;
    }


    public static RefWatcher getRefWatcher(Context context) {
        TApplication application = (TApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}
