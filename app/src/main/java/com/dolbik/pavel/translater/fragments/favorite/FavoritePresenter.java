package com.dolbik.pavel.translater.fragments.favorite;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.db.Repository;
import com.dolbik.pavel.translater.events.HistoryEvent;
import com.dolbik.pavel.translater.model.History;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.SingleSubscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


@InjectViewState
public class FavoritePresenter extends MvpPresenter<FavoriteView> {

    @Inject Repository repository;
    @Inject EventBus   bus;

    private CompositeSubscription compositeSbs;

    /** Коллекция содержит все данные. <br>
     *  The collection contains all the data.  */
    private List<History> allData = new ArrayList<>();


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        TApplication.getAppComponent().inject(this);

        compositeSbs = new CompositeSubscription();
        bus.register(this);
        getHistoryFromDB();
    }


    /** Получаем данные из БД. <br>
     *  Get data from DB*/
    private void getHistoryFromDB() {
        getViewState().showHideProgress(true);
        getViewState().showHideEmpty(false);
        Subscription sbs = repository.getFavoritesFromDb()
                .subscribe(new SingleSubscriber<List<History>>() {
                    @Override
                    public void onSuccess(List<History> data) {
                        getViewState().showHideProgress(false);
                        allData = data;
                        if (allData.isEmpty()) { getViewState().showHideEmpty(true); }
                        //Отлавливается в NotePresenter. Catch in NotePresenter.
                        bus.post(new HistoryEvent.IsFavoriteListEmpty(data.isEmpty()));
                        getViewState().setData(allData);
                    }

                    @Override
                    public void onError(Throwable error) {
                        getViewState().showHideProgress(false);
                        getViewState().showHideEmpty(true);
                        getViewState().showSnakeBar(error.getLocalizedMessage());
                        error.printStackTrace();
                    }
                });
        compositeSbs.add(sbs);
    }


    /** Добавляем или удаляем item из Favorite. <br>
     *  Add or remove item from Favorite. */
    void updateFavoriteHistoryItem(History history, int position) {
        compositeSbs.clear();
        history.setFavorite(!history.isFavorite());
        Subscription sbs = repository.updateFavoriteHistoryItem(history)
                .subscribe(new SingleSubscriber<History>() {
                    @Override
                    public void onSuccess(History value) {
                        allData.remove(position);
                        getViewState().notifyItemRemove(position);
                        if (allData.isEmpty()) {
                            getViewState().showHideEmpty(true);
                            //Отлавливается в NotePresenter. Catch in NotePresenter.
                            bus.post(new HistoryEvent.IsFavoriteListEmpty(allData.isEmpty()));
                        }
                        // Отлавливается в HistoryPresenter (Catch in HistoryPresenter)
                        bus.post(new HistoryEvent.UpdateHistoryList());
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }
                });
        compositeSbs.add(sbs);
    }


    //Посылается из HistoryPresenter, NotePresenter.
    //It is sent from HistoryPresenter, NotePresenter.
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HistoryEvent.UpdateFavoriteList event) {
        getHistoryFromDB();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
        compositeSbs.unsubscribe();
        allData    = null;
    }

}
