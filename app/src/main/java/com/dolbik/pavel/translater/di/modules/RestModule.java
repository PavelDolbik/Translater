package com.dolbik.pavel.translater.di.modules;


import android.content.Context;

import com.dolbik.pavel.translater.rest.RestApi;
import com.dolbik.pavel.translater.rest.RestService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RestModule {

    @Singleton
    @Provides
    public RestApi provideRest(Context context) {
        return RestService.getInstance().getRestApi(context);
    }
}
