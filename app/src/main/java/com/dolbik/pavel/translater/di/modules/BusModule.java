package com.dolbik.pavel.translater.di.modules;


import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BusModule {

    @Provides
    @Singleton
    public EventBus provideBus() {
        return EventBus.getDefault();
    }

}
