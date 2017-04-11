package com.dolbik.pavel.translater.db;


import android.util.Pair;

import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.model.Language;
import com.dolbik.pavel.translater.model.Translate;
import com.dolbik.pavel.translater.rest.RestApi;
import com.dolbik.pavel.translater.utils.Constants;
import com.google.gson.JsonElement;

import net.grandcentrix.tray.AppPreferences;

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
