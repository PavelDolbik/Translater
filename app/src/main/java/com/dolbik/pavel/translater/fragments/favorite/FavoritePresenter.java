package com.dolbik.pavel.translater.fragments.favorite;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.db.DataRepository;
import com.dolbik.pavel.translater.db.Repository;
import com.dolbik.pavel.translater.events.HistoryEvent;
import com.dolbik.pavel.translater.model.History;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import rx.SingleSubscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


@InjectViewState
public class FavoritePresenter extends MvpPresenter<FavoriteView> {

    private EventBus              bus;
    private Repository            repository;
    private CompositeSubscription compositeSbs;


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        bus          = EventBus.getDefault();
        repository   = new DataRepository();
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
                        if (data.isEmpty()) { getViewState().showHideEmpty(true); }
                        getViewState().setData(data);
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
                        getViewState().notifyItemRemove(position);
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


    //Посылается из HistoryPresenter.
    //It is sent from HistoryPresenter.
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void onEvent(HistoryEvent.UpdateFavoriteList event) {
        getHistoryFromDB();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
        compositeSbs.unsubscribe();
        repository = null;
    }

}
