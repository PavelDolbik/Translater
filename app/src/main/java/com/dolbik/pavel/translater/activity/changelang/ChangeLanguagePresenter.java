package com.dolbik.pavel.translater.activity.changelang;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.db.Repository;
import com.dolbik.pavel.translater.events.ChangeLangEvent;
import com.dolbik.pavel.translater.model.Language;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import rx.SingleSubscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


@InjectViewState
public class ChangeLanguagePresenter extends MvpPresenter<ChangeLanguageView> {

    @Inject Repository repository;
    private CompositeSubscription compositeSbs;
    private int direction = 0; // 1 - from, 2 - to.


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        TApplication.get().plusRepositoryComponent().inject(this);
        compositeSbs = new CompositeSubscription();
        getDataFromDb();
    }


    private void getDataFromDb() {
        Subscription sbs = repository.getLangsFromDB()
                .subscribe(new SingleSubscriber<List<Language>>() {
                    @Override
                    public void onSuccess(List<Language> data) {
                        getViewState().setData(data);
                        getViewState().hideProgress();
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                        getViewState().hideProgress();
                        getViewState().showSnakeBar(error.getLocalizedMessage());
                    }
                });
        compositeSbs.add(sbs);
    }


    void setDirection(int direction) {
        this.direction = direction;
    }


    // Отлавливаются в TranslatePresenter.
    // Catch in TranslatePresenter.
    void changeLanguage(Language language) {
        switch (direction) {
            case 1: EventBus.getDefault().postSticky(new ChangeLangEvent.From(language)); break;
            case 2: EventBus.getDefault().postSticky(new ChangeLangEvent.To(language));   break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSbs.unsubscribe();
        TApplication.get().clearRepositoryComponent();
    }
}
