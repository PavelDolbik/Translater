package com.dolbik.pavel.translater.fragments.translate;

import android.text.TextUtils;
import android.util.Pair;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.db.DataRepository;
import com.dolbik.pavel.translater.db.Repository;
import com.dolbik.pavel.translater.events.ChangeLangEvent;
import com.dolbik.pavel.translater.events.HistoryEvent;
import com.dolbik.pavel.translater.model.History;
import com.dolbik.pavel.translater.model.Language;
import com.dolbik.pavel.translater.model.ResultTranslate;
import com.dolbik.pavel.translater.rest.ErrorHandler;
import com.dolbik.pavel.translater.utils.Constants;

import net.grandcentrix.tray.AppPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Observable;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


@InjectViewState
public class TranslatePresenter extends MvpPresenter<TranslateView> {

    private EventBus              bus;
    private Repository            repository;
    private TApplication          application;
    private AppPreferences        pref;
    private CompositeSubscription compositeSbs;

    /** Текущее направление перевода. <br>
     *  Current direction of translation. */
    private Pair<Language, Language> languagePair;

    /** Направление перевода. <br>
     *  Direction of translation. */
    private String translateDirection;

    /** Объект, который в данный момент переводится. <br>
     *  The object that is currently being translated. */
    private History currentHistory;

    private Subscription translateSbs;


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        bus          = EventBus.getDefault();
        repository   = new DataRepository();
        compositeSbs = new CompositeSubscription();
        bus.register(this);

        prepareInstallLangsFromJson();
    }


    /** При первом запуске, заполняем БД и отображаем направление перевода. <br>
     *  At the first start, fill out the database and display the direction of the translation. */
    private void prepareInstallLangsFromJson() {
        Subscription sbs = repository.preInstallLangs()
                .subscribe(new Subscriber<Pair<Language, Language>>() {
                    @Override
                    public void onNext(Pair<Language, Language> pair) {
                        languagePair = pair;
                        getViewState().showToolbarView();
                        updateTranslationDirection();
                        translateLangForCurrentLocale();
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
                            languagePair = pair;
                            updateTranslationDirection();
                        }

                        @Override
                        public void onCompleted() {
                            //Пришло событие из HistoryFragment.
                            // An event from HistoryFragment has come.
                            if (currentHistory != null) {
                                languagePair = new Pair<>(currentHistory.getFromLang(), currentHistory.getToLang());
                                updateTranslationDirection();
                                updateStorePair();
                                getViewState().setTextForTranslate(currentHistory.getText());
                                getViewState().showHideFavoriteBtn(true, currentHistory.isFavorite());
                                getViewState().showViewStub(TranslateFragmentState.SHOW_TRANSLATE, currentHistory.getTranslate());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            getViewState().showSnakeBar(ErrorHandler.getInstance().getErrorMessage(e));
                        }
                    });
            compositeSbs.add(sbs);
        }
    }


    void translateText(String text, boolean forceTranslate) {
        if (TextUtils.isEmpty(text)) {
            clear();
        } else {
            getViewState().showCleanBtn();
            if (getApplication().isConnected()) {
                unsubscribeTranslateSbs();
                if (currentHistory == null || !currentHistory.getText().equals(text) || forceTranslate) {
                    getViewState().showViewStub(TranslateFragmentState.SHOW_PROGRESS, null);

                    translateSbs = repository.getResultTranslate(text, translateDirection, languagePair)
                            .subscribe(new Subscriber<ResultTranslate>() {
                                @Override
                                public void onNext(ResultTranslate result) {
                                    currentHistory = result.getHistory();
                                    getViewState().showHideFavoriteBtn(true, currentHistory.isFavorite());
                                    getViewState().showViewStub(
                                            TranslateFragmentState.SHOW_TRANSLATE,
                                            result.getTranslate().getText().get(0));
                                }

                                @Override
                                public void onCompleted() {}

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    getViewState().showHideFavoriteBtn(false, false);
                                    getViewState().showViewStub(TranslateFragmentState.IDLE, null);
                                    getViewState().showSnakeBar(ErrorHandler.getInstance().getErrorMessage(e));
                                }
                            });
                }
            } else {
                getViewState().showHideFavoriteBtn(false, false);
                getViewState().showViewStub(TranslateFragmentState.SHOW_ERROR, null);
            }
        }
    }


    /** Направление перевода. Параметр в запросе API. <br>
     *  Direction of translation. Parameter in the API request. */
    private void setTranslateDirection() {
        if (languagePair != null) {
            translateDirection = new StringBuilder()
                    .append(languagePair.first.getCode())
                    .append("-")
                    .append(languagePair.second.getCode()).toString();
        }
    }


    /** Обновляем направление перевода в UI. <br>
     *  Update the direction of the translation in UI. */
    private void updateTranslationDirection() {
        setTranslateDirection();
        getViewState().updateTranslateDirection(languagePair.first.getName(), languagePair.second.getName());
    }


    /** Обновляем значения хранящиеся в preferences. <br>
     *  Update the values stored in preferences. */
    private void updateStorePair() {
        Observable.fromCallable(() -> {
            getPref().put(Constants.DIRC_FROM_CODE, languagePair.first.getCode());
            getPref().put(Constants.DIRC_FROM_NAME, languagePair.first.getName());
            getPref().put(Constants.DIRC_TO_CODE,   languagePair.second.getCode());
            getPref().put(Constants.DIRC_TO_NAME,   languagePair.second.getName());
            return true;
        }).subscribeOn(Schedulers.io()).subscribe();
    }


    //Посылается из ChangeLanguagePresenter.
    //It is sent from ChangeLanguagePresenter.
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(ChangeLangEvent.From event) {
        bus.removeStickyEvent(event);
        Language language     = event.getLanguage();
        Language tempLanguage = languagePair.second;
        if (language.getId() == tempLanguage.getId()) {
            tempLanguage = languagePair.first;
        }
        languagePair = new Pair<>(language, tempLanguage);
        changeLangUpdate();
    }


    //Посылается из ChangeLanguagePresenter.
    //It is sent from ChangeLanguagePresenter.
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
        public void onEvent(ChangeLangEvent.To event) {
        bus.removeStickyEvent(event);
        Language language     = event.getLanguage();
        Language tempLanguage = languagePair.first;
        if (language.getId() == tempLanguage.getId()) {
            tempLanguage = languagePair.second;
        }
        languagePair = new Pair<>(tempLanguage, language);
        changeLangUpdate();
    }


    //Посылается из HistoryFragment.
    //It is sent from HistoryFragment.
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(HistoryEvent.Click event) {
        bus.removeStickyEvent(event);
        currentHistory = event.getHistory();
    }


    private void changeLangUpdate() {
        updateTranslationDirection();
        updateStorePair();
        translateText(currentHistory.getText(), true);
    }


    void swap() {
        languagePair = new Pair<>(languagePair.second, languagePair.first);
        changeLangUpdate();
    }


    void clear() {
        getViewState().hideCleanBtn();
        getViewState().showHideFavoriteBtn(false, false);
        getViewState().showViewStub(TranslateFragmentState.IDLE, null);
        currentHistory = null;
    }


    void favorite() {
        compositeSbs.clear();
        currentHistory.setFavorite(!currentHistory.isFavorite());
        Subscription sbs = repository.updateFavoriteHistoryItem(currentHistory)
                .subscribe(new SingleSubscriber<History>() {
                    @Override
                    public void onSuccess(History value) {
                        currentHistory = value;
                        getViewState().showHideFavoriteBtn(true, currentHistory.isFavorite());
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }
                });
        compositeSbs.add(sbs);
    }


    Pair<Language, Language> getLanguagePair() {
        return languagePair;
    }


    private void unsubscribeTranslateSbs() {
        if (translateSbs != null) {
            translateSbs.unsubscribe();
        }
    }


    private TApplication getApplication() {
        if (application == null) {
            application = TApplication.getInstance();
        }
        return application;
    }


    private AppPreferences getPref() {
        if (pref == null || application == null) {
            pref = new AppPreferences(getApplication());
        }
        return pref;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSbs.unsubscribe();
        unsubscribeTranslateSbs();
        bus.unregister(this);
        currentHistory = null;
        bus            = null;
        repository     = null;
    }
}
