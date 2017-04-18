package com.dolbik.pavel.translater.db;


import android.util.Pair;

import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.model.History;
import com.dolbik.pavel.translater.model.Language;
import com.dolbik.pavel.translater.model.ResultTranslate;
import com.dolbik.pavel.translater.model.Translate;
import com.dolbik.pavel.translater.rest.RestApi;
import com.dolbik.pavel.translater.utils.Constants;
import com.google.gson.JsonElement;
import com.j256.ormlite.stmt.QueryBuilder;

import net.grandcentrix.tray.AppPreferences;

import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class DataRepository implements Repository {

    private TApplication   application;
    private DatabaseHelper dbHelper;
    private AppPreferences pref;
    private RestApi        restApi;


    @Override
    public Observable<Pair<Language, Language>> preInstallLangs() {
        return Observable
                .fromCallable(() -> {
                    PreInstallLangs preInstallLangs = new PreInstallLangs(getTApplication(), getPref(),getDbHelper());
                    return preInstallLangs.checkDirectionTranslate();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Observable<Pair<Language, Language>> getAllLangs() {
        return Observable
                .fromCallable(() -> getPref().getString(Constants.CURRENT_LOCATION, ""))
                .flatMap(new Func1<String, Observable<JsonElement>>() {
                    @Override
                    public Observable<JsonElement> call(String s) {
                        String currentLocale = Locale.getDefault().getLanguage();
                        if (!currentLocale.equals(s)) {
                            return getRestApi().getAllLangs(currentLocale).toObservable();
                        }
                        return null;
                    }
                })
                .map(jsonElement -> {
                    UpdateAllLangs updateAllLangs = new UpdateAllLangs(getPref(), getDbHelper());
                    return updateAllLangs.update(jsonElement.toString());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<Translate> getTranslate(String text, String lang) {
        return getRestApi().getTranslate(text, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<List<Language>> getLangsFromDB() {
        return Single
                .fromCallable(() ->
                        getDbHelper().getLanguageDao().queryBuilder()
                        .orderBy(DbContract.Langs.LANGS_NAME, true).query())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<History> getHistoryEntity(String text, String direction) {
        return Single.fromCallable(() -> {
            QueryBuilder<History, Integer> qb = getDbHelper().getHistoryDao().queryBuilder();
            qb.where().eq(DbContract.History.DIRECTION, direction)
                    .and().like(DbContract.History.TEXT, prepareLikeQuery(text));
            return qb.queryForFirst();
        }).subscribeOn(Schedulers.io());
    }


    private String prepareLikeQuery(String query) {
        return "%" + query
                .replaceAll("\\\\", "\\\\\\\\")
                .replaceAll("_", "\\\\_")
                .replaceAll("%", "\\\\%")
                .replaceAll("'", "''") + "%' ESCAPE '\\";
    }


    @Override
    public Observable<ResultTranslate> getResultTranslate(String text, String lang, Pair<Language, Language> pair) {
        Observable<History> historyFromDB = getHistoryEntity(text, lang)
                .toObservable().subscribeOn(Schedulers.io());
        Observable<Translate> translateFronServer = getTranslate(text, lang)
                .toObservable().subscribeOn(Schedulers.io());
        return Observable
                .zip(
                        historyFromDB,
                        translateFronServer,
                        (history, translate) -> new ResultTranslate(translate, history))
                .doOnNext(result -> {
                    if (result.getHistory() == null) {
                        History history = new History();
                        history.setText(text);
                        history.setTranslate(result.getTranslate().getText().get(0));
                        history.setDirection(lang);
                        history.setFavorite(false);
                        history.setFromLang(pair.first);
                        history.setToLang(pair.second);

                        HistoryDB historyDB = new HistoryDB(getDbHelper());
                        long id = historyDB.saveInHistory(history);
                        history.setId((int) id);
                        result.setHistory(history);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<List<History>> getHistoryFromDb() {
        return Single
                .fromCallable(() -> getDbHelper().getHistoryDao().queryBuilder()
                        .orderBy(DbContract.History.ID, false).query())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<List<History>> getFavoritesFromDb() {
        return Single
                .fromCallable(() -> getDbHelper().getHistoryDao().queryBuilder()
                        .orderBy(DbContract.History.ID, false)
                        .where().eq(DbContract.History.IS_FAVORITE, Boolean.TRUE)
                        .query())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<History> updateFavoriteHistoryItem(History history) {
        return Single
                .fromCallable(() -> new HistoryDB(getDbHelper()).updateFavorite(history))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private TApplication getTApplication() {
        if (application == null) {
            application = TApplication.getInstance();
        }
        return application;
    }


    private DatabaseHelper getDbHelper() {
        if (application == null || dbHelper == null) {
            dbHelper = getTApplication().getHelper();
        }
        return dbHelper;
    }


    private AppPreferences getPref() {
        if (application == null || pref == null) {
            pref = new AppPreferences(getTApplication());
        }
        return pref;
    }


    private RestApi getRestApi() {
        if (application == null || restApi == null) {
            restApi = getTApplication().getRestApi();
        }
        return restApi;
    }

}
