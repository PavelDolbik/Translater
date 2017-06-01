package com.dolbik.pavel.translater.fragments.note;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.db.Repository;
import com.dolbik.pavel.translater.events.HistoryEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import rx.SingleSubscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


@InjectViewState
public class NotePresenter extends MvpPresenter<NoteView> {

    @Inject Repository repository;
    @Inject EventBus   bus;

    private CompositeSubscription compositeSbs;

    private boolean isHistoryEmpty  = true;
    private boolean isFavoriteEmpty = true;
    private int     currentFragmentPosition = 0;


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        TApplication.getAppComponent().inject(this);
        bus.register(this);
    }


    /** Определяем позицию текущего пользователя и скрываем/показываем меню. <br>
     *  Determine the position of the current user and hide/show the menu. */
    void setCurrentFragmentPosition(int position) {
        currentFragmentPosition = position;
        switch (currentFragmentPosition) {
            case 0: getViewState().showHideRemoveItem(!isHistoryEmpty);  break;
            case 1: getViewState().showHideRemoveItem(!isFavoriteEmpty); break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HistoryEvent.IsHistoryListEmpty event) {
        isHistoryEmpty = event.isEmpty();
        if (currentFragmentPosition == 0) {
            getViewState().showHideRemoveItem(!isHistoryEmpty);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HistoryEvent.IsFavoriteListEmpty event) {
        isFavoriteEmpty = event.isEmpty();
        if (currentFragmentPosition == 1) {
            getViewState().showHideRemoveItem(!isFavoriteEmpty);
        }
    }


    void removeItems(int position) {
        switch (position) {
            case 0: removeAllHistory();  break;
            case 1: removeAllFavorite(); break;
        }
    }


    private void removeAllHistory() {
        Subscription sbs = repository.deleteAllHistory()
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean value) {
                        //Отлавливается в HistoryPresenter. Catch in HistoryPresenter.
                        isHistoryEmpty = value;
                        bus.post(new HistoryEvent.UpdateHistoryList());
                        getViewState().showHideRemoveItem(!isHistoryEmpty);
                    }

                    @Override
                    public void onError(Throwable error) { error.printStackTrace(); }
                });
        getCompositeSbs().add(sbs);
    }


    private void removeAllFavorite() {
        Subscription sbs = repository.deleteAllFavorite()
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean value) {
                        isFavoriteEmpty = value;
                        //Отлавливается в FavoritePresenter. Catch in FavoritePresenter.
                        bus.post(new HistoryEvent.UpdateFavoriteList());
                        //Отлавливается в HistoryPresenter. Catch in HistoryPresenter.
                        bus.post(new HistoryEvent.UpdateHistoryList());
                        getViewState().showHideRemoveItem(!isFavoriteEmpty);
                    }

                    @Override
                    public void onError(Throwable error) { error.printStackTrace(); }
                });
        getCompositeSbs().add(sbs);
    }


    private CompositeSubscription getCompositeSbs() {
        if (compositeSbs == null) {
            compositeSbs = new CompositeSubscription();
        }
        return compositeSbs;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
        if (compositeSbs != null) { compositeSbs.unsubscribe(); }
    }

}
