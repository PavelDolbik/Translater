package com.dolbik.pavel.translater.fragments.translate;

import android.util.Pair;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.db.DataRepository;
import com.dolbik.pavel.translater.db.Repository;
import com.dolbik.pavel.translater.model.Language;

import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


@InjectViewState
public class TranslatePresenter extends MvpPresenter<TranslateView> {


    private Repository            repository;
    private TApplication          application;
    private CompositeSubscription compositeSbs;


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        repository   = new DataRepository();
        compositeSbs = new CompositeSubscription();

        prepareInstallLangsFromJson();
    }


    /** При первом запуске, заполняем БД и отображаем направление перевода. <br>
     *  At the first start, fill out the database and display the direction of the translation. */
    private void prepareInstallLangsFromJson() {
        Subscription sbs = repository.preInstallLangs()
                .subscribe(new Subscriber<Pair<Language, Language>>() {
                    @Override
                    public void onNext(Pair<Language, Language> pair) {
                        translateLangForCurrentLocale();
                        getViewState().showToolbarView();
                        getViewState().updateTranslateDirection(pair.first.getName(), pair.second.getName());
                    }

                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
        });
        compositeSbs.add(sbs);
    }


    /** Переводим языки в соответствии с текущей локалью. <br>
     *  Translate languages according to the current locale. */
    private void translateLangForCurrentLocale() {
        if (getApplication().isConnected()) {
            Subscription sbs = repository.getAllLangs()
                    .subscribe(new Subscriber<Pair<Language, Language>>() {
                        @Override
                        public void onNext(Pair<Language, Language> pair) {
                            getViewState().updateTranslateDirection(pair.first.getName(), pair.second.getName());
                        }

                        @Override
                        public void onCompleted() {}

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });
            compositeSbs.add(sbs);
        }
    }


    private TApplication getApplication() {
        if (application == null) {
            application = TApplication.getInstance();
        }
        return application;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSbs.unsubscribe();
        repository = null;
    }
}
