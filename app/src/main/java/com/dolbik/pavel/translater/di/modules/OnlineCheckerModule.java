package com.dolbik.pavel.translater.di.modules;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class OnlineCheckerModule {

    @Provides
    @Singleton
    public Boolean provideOnlineChecker(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }
}
