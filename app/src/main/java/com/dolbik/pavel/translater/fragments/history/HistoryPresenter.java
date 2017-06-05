package com.dolbik.pavel.translater.fragments.history;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.db.Repository;
import com.dolbik.pavel.translater.events.HistoryEvent;
import com.dolbik.pavel.translater.model.History;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import rx.SingleSubscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


@InjectViewState
public class HistoryPresenter extends MvpPresenter<HistoryView> {

    @Inject Repository repository;
    @Inject EventBus   bus;

    private CompositeSubscription compositeSbs;


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        TApplication.get().plusRepositoryComponent().inject(this);

        compositeSbs = new CompositeSubscription();
        bus.register(this);
        getHistoryFromDB();
    }


    /** Получаем данные из БД. <br>
     *  Get data from DB*/
    private void getHistoryFromDB() {
        getViewState().showHideProgress(true);
        getViewState().showHideEmpty(false);
        Subscription sbs = repository.getHistoryFromDb()
                .subscribe(new SingleSubscriber<List<History>>() {
                    @Override
                    public void onSuccess(List<History> data) {
                        getViewState().showHideProgress(false);
                        if (data.isEmpty()) { getViewState().showHideEmpty(true); }
                        getViewState().setData(data);
                        //Отлавливается в NotePresenter. Catch in NotePresenter.
                        bus.post(new HistoryEvent.IsHistoryListEmpty(data.isEmpty()));
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
                        getViewState().notifyItemChange(position);
                        // Отлавливается в FavoritePresenter (Catch in FavoritePresenter)
                        EventBus.getDefault().post(new HistoryEvent.UpdateFavoriteList());
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }
                });
        compositeSbs.add(sbs);
    }


    //Посылается из FavoritePresenter, NotePresenter.
    //It is sent from FavoritePresenter, NotePresenter.
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HistoryEvent.UpdateHistoryList event) {
        getHistoryFromDB();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
        compositeSbs.unsubscribe();
        TApplication.get().clearRepositoryComponent();
    }

}
