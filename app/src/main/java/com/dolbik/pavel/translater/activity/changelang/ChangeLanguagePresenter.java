package com.dolbik.pavel.translater.activity.changelang;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.db.DataRepository;
import com.dolbik.pavel.translater.db.Repository;
import com.dolbik.pavel.translater.model.Language;

import java.util.List;

import rx.SingleSubscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


@InjectViewState
public class ChangeLanguagePresenter extends MvpPresenter<ChangeLanguageView> {

    private TApplication          application;
    private Repository            repository;
    private CompositeSubscription compositeSbs;


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        repository   = new DataRepository();
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
