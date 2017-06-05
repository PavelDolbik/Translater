package com.dolbik.pavel.translater.di.components;


import com.dolbik.pavel.translater.di.modules.BusModule;
import com.dolbik.pavel.translater.di.modules.ContextModule;
import com.dolbik.pavel.translater.di.modules.DbModule;
import com.dolbik.pavel.translater.di.modules.OnlineCheckerModule;
import com.dolbik.pavel.translater.di.modules.PrefModule;
import com.dolbik.pavel.translater.di.modules.RepositoryModule;
import com.dolbik.pavel.translater.di.modules.RestModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
                ContextModule.class,
                PrefModule.class,
                BusModule.class,
                OnlineCheckerModule.class,
                RestModule.class,
                DbModule.class})
public interface AppComponent {

    RepositoryComponent plusRepositoryComponent(RepositoryModule repositoryModule);

}
