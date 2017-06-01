package com.dolbik.pavel.translater.di.modules;


import android.content.Context;

import net.grandcentrix.tray.AppPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PrefModule {

    @Provides
    @Singleton
    public AppPreferences providePref(Context context) {
        return new AppPreferences(context);
    }

}
