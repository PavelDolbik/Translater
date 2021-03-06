package com.dolbik.pavel.translater.di.modules;


import android.content.Context;

import com.dolbik.pavel.translater.db.DataRepository;
import com.dolbik.pavel.translater.db.DatabaseHelper;
import com.dolbik.pavel.translater.db.Repository;
import com.dolbik.pavel.translater.di.scope.PresenterScope;
import com.dolbik.pavel.translater.rest.RestApi;

import net.grandcentrix.tray.AppPreferences;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    @PresenterScope
    public Repository provideRepository(Context context, RestApi restApi, DatabaseHelper db, AppPreferences pref) {
        return new DataRepository(context, restApi, db, pref);
    }

}
