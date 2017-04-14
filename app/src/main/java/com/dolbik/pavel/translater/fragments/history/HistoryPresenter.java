package com.dolbik.pavel.translater.fragments.history;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.db.DataRepository;
import com.dolbik.pavel.translater.db.Repository;
import com.dolbik.pavel.translater.model.History;

import java.util.List;

import rx.SingleSubscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


@InjectViewState
public class HistoryPresenter extends MvpPresenter<HistoryView> {

    private Repository            repository;
    private CompositeSubscription compositeSbs;


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        repository   = new DataRepository();
        compositeSbs = new CompositeSubscription();
        getHistoryFromDB();
    }


    /** Получаем данные из БД. <br>
     *  Get data from DB*/
    private void getHistoryFromDB() {
        getViewState().showHideProgress(true);
        Subscription sbs = repository.getHistoryFromDb()
                .subscribe(new SingleSubscriber<List<History>>() {
                    @Override
                    public void onSuccess(List<History> data) {
                        getViewState().showHideProgress(false);
                        getViewState().setData(data);
                    }

                    @Override
                    public void onError(Throwable error) {
                        getViewState().showHideProgress(false);
                        getViewState().showSnakeBar(error.getLocalizedMessage());
                        error.printStackTrace();
                    }
                });
        compositeSbs.add(sbs);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSbs.unsubscribe();
        repository = null;
    }

}
