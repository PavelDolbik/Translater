package com.dolbik.pavel.translater.db;


import android.content.Context;
import android.util.Pair;

import com.dolbik.pavel.translater.model.History;
import com.dolbik.pavel.translater.model.Language;
import com.dolbik.pavel.translater.model.ResultTranslate;
import com.dolbik.pavel.translater.model.Translate;
import com.dolbik.pavel.translater.rest.RestApi;
import com.dolbik.pavel.translater.utils.Constants;
import com.google.gson.JsonElement;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import net.grandcentrix.tray.AppPreferences;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class DataRepository implements Repository {

    private Context        context;
    private DatabaseHelper dbHelper;
    private AppPreferences pref;
    private RestApi        restApi;


    public DataRepository(Context context, RestApi restApi, DatabaseHelper dbHelper, AppPreferences pref) {
        this.context  = context;
        this.restApi  = restApi;
        this.dbHelper = dbHelper;
        this.pref     = pref;
    }


    @Override
    public Observable<Pair<Language, Language>> preInstallLangs() {
        return Observable
                .fromCallable(() -> {
                    PreInstallLangs preInstallLangs = new PreInstallLangs(context, pref, dbHelper);
                    return preInstallLangs.checkDirectionTranslate();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Observable<Pair<Language, Language>> getAllLangs() {
        return Observable
                .fromCallable(() -> pref.getString(Constants.CURRENT_LOCATION, ""))
                .flatMap(new Func1<String, Observable<JsonElement>>() {
                    @Override
                    public Observable<JsonElement> call(String s) {
                        String currentLocale = Locale.getDefault().getLanguage();
                        if (!currentLocale.equals(s)) {
                            return restApi.getAllLangs(currentLocale).toObservable();
                        }
                        return null;
                    }
                })
                .map(jsonElement -> {
                    UpdateAllLangs updateAllLangs = new UpdateAllLangs(pref, dbHelper);
                    return updateAllLangs.update(jsonElement.toString());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<Translate> getTranslate(String text, String lang) {
        return restApi
                .getTranslate(text, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<List<Language>> getLangsFromDB() {
        return Single
                .fromCallable(() ->
                        dbHelper.getLanguageDao().queryBuilder()
                        .orderBy(DbContract.Langs.LANGS_NAME, true).query())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<History> getHistoryEntity(String text, String direction) {
        return Single
                .fromCallable(() -> {
                    QueryBuilder<History, Integer> qb = dbHelper.getHistoryDao().queryBuilder();
                    qb.where().eq(DbContract.History.DIRECTION, direction)
                            .and().like(DbContract.History.TEXT, prepareLikeQuery(text));
                    return qb.queryForFirst();
                })
                .subscribeOn(Schedulers.io());
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

                        HistoryDB historyDB = new HistoryDB(dbHelper);
                        long id = historyDB.saveInHistory(history);
                        history.setId((int) id);
                        result.setHistory(history);
                    } else {
                        if (!result.getHistory().isHistory()) {
                            result.getHistory().setHistory(true);
                            try {
                                dbHelper.getHistoryDao().update(result.getHistory());
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<List<History>> getHistoryFromDb() {
        return Single
                .fromCallable(() -> dbHelper.getHistoryDao().queryBuilder()
                        .orderBy(DbContract.History.ID, false)
                        .where().eq(DbContract.History.IS_HISTORY, Boolean.TRUE)
                        .query())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<List<History>> getFavoritesFromDb() {
        return Single
                .fromCallable(() -> dbHelper.getHistoryDao().queryBuilder()
                        .orderBy(DbContract.History.ID, false)
                        .where().eq(DbContract.History.IS_FAVORITE, Boolean.TRUE)
                        .query())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<History> updateFavoriteHistoryItem(History history) {
        return Single
                .fromCallable(() -> new HistoryDB(dbHelper).updateFavorite(history))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<Boolean> deleteAllHistory() {
        return Single
                .fromCallable(() -> {
                    UpdateBuilder<History, Integer> ub = dbHelper.getHistoryDao().updateBuilder();
                    ub.where().eq(DbContract.History.IS_FAVORITE, Boolean.TRUE);
                    ub.updateColumnValue(DbContract.History.IS_HISTORY, Boolean.FALSE);
                    ub.update();

                    DeleteBuilder<History, Integer> db = dbHelper.getHistoryDao().deleteBuilder();
                    db.where().eq(DbContract.History.IS_HISTORY, Boolean.TRUE);
                    db.delete();

                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Single<Boolean> deleteAllFavorite() {
        return Single
                .fromCallable(() -> {
                    UpdateBuilder<History, Integer> ub = dbHelper.getHistoryDao().updateBuilder();
                    ub.where().eq(DbContract.History.IS_FAVORITE, Boolean.TRUE);
                    ub.updateColumnValue(DbContract.History.IS_FAVORITE, Boolean.FALSE);
                    ub.update();

                    DeleteBuilder<History, Integer> db = dbHelper.getHistoryDao().deleteBuilder();
                    db.where()
                            .eq(DbContract.History.IS_FAVORITE, Boolean.FALSE)
                            .and().eq(DbContract.History.IS_HISTORY, Boolean.FALSE);
                    db.delete();
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
