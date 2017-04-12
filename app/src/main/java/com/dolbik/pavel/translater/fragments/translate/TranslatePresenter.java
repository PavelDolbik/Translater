package com.dolbik.pavel.translater.fragments.translate;

import android.text.TextUtils;
import android.util.Pair;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.db.DataRepository;
import com.dolbik.pavel.translater.db.Repository;
import com.dolbik.pavel.translater.model.Language;
import com.dolbik.pavel.translater.model.Translate;
import com.dolbik.pavel.translater.rest.ErrorHandler;

import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


@InjectViewState
public class TranslatePresenter extends MvpPresenter<TranslateView> {


    private Repository            repository;
    private TApplication          application;
    private CompositeSubscription compositeSbs;

    /** Текущее направление перевода. <br>
     *  Current direction of translation. */
    private Pair<Language, Language> languagePair;

    /** Направление перевода. <br>
     *  Direction of translation. */
    private String translateDirection;

    /** Текущий текст который переводится. <br>
     *  The current text is translated. */
    private String translateText;

    private Subscription translateSbs;


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
                        languagePair = pair;
                        setTranslateDirection();
                        translateLangForCurrentLocale();
                        getViewState().showToolbarView();
                        getViewState().updateTranslateDirection(languagePair.first.getName(), languagePair.second.getName());
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
                            setTranslateDirection();
                            getViewState().updateTranslateDirection(languagePair.first.getName(), languagePair.second.getName());
                        }

                        @Override
                        public void onCompleted() {}

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            getViewState().showSnakeBar(ErrorHandler.getInstance().getErrorMessage(e));
                        }
                    });
            compositeSbs.add(sbs);
        }
    }


    void translateText(String text) {
        if (TextUtils.isEmpty(text)) {
            clear();
        } else {
            getViewState().showCleanBtn();
            if (getApplication().isConnected()) {
                unsubscribeTranslateSbs();
                if (translateText == null || !translateText.equals(text)) {
                    translateText = text;
                    getViewState().showViewStub(TranslateFragmentState.SHOW_PROGRESS, null);
                    translateSbs = repository.getTranslate(text, translateDirection)
                            .subscribe(new SingleSubscriber<Translate>() {
                                @Override
                                public void onSuccess(Translate value) {
                                    getViewState().showHideFavoriteBtn(true);
                                    getViewState().showViewStub(
                                            TranslateFragmentState.SHOW_TRANSLATE, value.getText().get(0));
                                }

                                @Override
                                public void onError(Throwable error) {
                                    error.printStackTrace();
                                    getViewState().showHideFavoriteBtn(false);
                                    getViewState().showViewStub(TranslateFragmentState.IDLE, null);
                                    getViewState().showSnakeBar(ErrorHandler.getInstance().getErrorMessage(error));
                                }
                            });
                }
            } else {
                getViewState().showHideFavoriteBtn(false);
                getViewState().showViewStub(TranslateFragmentState.SHOW_ERROR, null);
            }
        }
    }


    private void setTranslateDirection() {
        if (languagePair != null) {
            translateDirection = new StringBuilder()
                    .append(languagePair.first.getCode())
                    .append("-")
                    .append(languagePair.second.getCode()).toString();
        }
    }


    void clear() {
        getViewState().hideCleanBtn();
        getViewState().showHideFavoriteBtn(false);
        getViewState().showViewStub(TranslateFragmentState.IDLE, null);
    }


    public Pair<Language, Language> getLanguagePair() {
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSbs.unsubscribe();
        unsubscribeTranslateSbs();
        repository = null;
    }
}
