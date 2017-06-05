package com.dolbik.pavel.translater.di.components;

import com.dolbik.pavel.translater.activity.changelang.ChangeLanguagePresenter;
import com.dolbik.pavel.translater.di.modules.RepositoryModule;
import com.dolbik.pavel.translater.di.scope.PresenterScope;
import com.dolbik.pavel.translater.fragments.favorite.FavoritePresenter;
import com.dolbik.pavel.translater.fragments.history.HistoryPresenter;
import com.dolbik.pavel.translater.fragments.note.NotePresenter;
import com.dolbik.pavel.translater.fragments.translate.TranslatePresenter;

import dagger.Subcomponent;

@Subcomponent(modules = {RepositoryModule.class})
@PresenterScope
public interface RepositoryComponent {

    void inject(TranslatePresenter      presenter);
    void inject(FavoritePresenter       presenter);
    void inject(HistoryPresenter        presenter);
    void inject(NotePresenter           presenter);
    void inject(ChangeLanguagePresenter presenter);
}
