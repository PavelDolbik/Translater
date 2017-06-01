package com.dolbik.pavel.translater.di.modules;


import android.content.Context;

import com.dolbik.pavel.translater.db.DatabaseHelper;
import com.dolbik.pavel.translater.db.DatabaseManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    @Singleton
    @Provides
    public DatabaseHelper provideDb(Context context) {
        return DatabaseManager.getInstance().getHelper(context);
    }

}
