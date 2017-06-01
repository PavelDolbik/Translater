package com.dolbik.pavel.translater.di.components;


import com.dolbik.pavel.translater.activity.changelang.ChangeLanguagePresenter;
import com.dolbik.pavel.translater.di.modules.BusModule;
import com.dolbik.pavel.translater.di.modules.ContextModule;
import com.dolbik.pavel.translater.di.modules.DbModule;
import com.dolbik.pavel.translater.di.modules.OnlineCheckerModule;
import com.dolbik.pavel.translater.di.modules.PrefModule;
import com.dolbik.pavel.translater.di.modules.RepositoryModule;
import com.dolbik.pavel.translater.di.modules.RestModule;
import com.dolbik.pavel.translater.fragments.favorite.FavoritePresenter;
import com.dolbik.pavel.translater.fragments.history.HistoryPresenter;
import com.dolbik.pavel.translater.fragments.note.NotePresenter;
import com.dolbik.pavel.translater.fragments.translate.TranslatePresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
                ContextModule.class,
                PrefModule.class,
                BusModule.class,
                OnlineCheckerModule.class,
                RestModule.class,
                DbModule.class,
                RepositoryModule.class})
public interface AppComponent {

    void inject(TranslatePresenter      presenter);
    void inject(FavoritePresenter       presenter);
    void inject(HistoryPresenter        presenter);
    void inject(NotePresenter           presenter);
    void inject(ChangeLanguagePresenter presenter);

}
