package com.dolbik.pavel.translater.di.components;


import com.dolbik.pavel.translater.di.modules.BusModule;
import com.dolbik.pavel.translater.di.modules.ContextModule;
import com.dolbik.pavel.translater.di.modules.OnlineCheckerModule;
import com.dolbik.pavel.translater.di.modules.PrefModule;
import com.dolbik.pavel.translater.fragments.translate.TranslatePresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, PrefModule.class, BusModule.class, OnlineCheckerModule.class})
public interface AppComponent {

    void inject(TranslatePresenter presenter);

}
