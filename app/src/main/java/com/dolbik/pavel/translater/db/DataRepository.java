package com.dolbik.pavel.translater.db;


import android.util.Pair;

import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.model.Language;
import com.dolbik.pavel.translater.rest.RestApi;
import com.dolbik.pavel.translater.utils.Constants;
import com.google.gson.JsonElement;

import net.grandcentrix.tray.AppPreferences;

import java.util.Locale;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class DataRepository implements Repository {

    private TApplication   application;
    private DbOpenHelper   dbOpenHelper;
    private AppPreferences pref;
    private RestApi        restApi;


    @Override
    public Observable<Pair<Language, Language>> preInstallLangs() {
        return Observable.fromCallable(new Callable<Pair<Language, Language>>() {
            @Override
            public Pair<Language, Language> call() throws Exception {
                PreInstallLangs preInstallLangs = new PreInstallLangs(getTApplication(), getPref(),getDbOpenHelper());
                return preInstallLangs.checkDirectionTranslate();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Observable<Pair<Language, Language>> getAllLangs() {
        return Observable
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return getPref().getString(Constants.CURRENT_LOCATION, "");
                    }
                })
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
                .map(new Func1<JsonElement, Pair<Language, Language>>() {
                    @Override
                    public Pair<Language, Language> call(JsonElement jsonElement) {
                        UpdateAllLangs updateAllLangs = new UpdateAllLangs(getPref(), getDbOpenHelper());
                        return updateAllLangs.update(jsonElement.toString());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private TApplication getTApplication() {
        if (application == null) {
            application = TApplication.getInstance();
        }
        return application;
    }


    private DbOpenHelper getDbOpenHelper() {
        if (application == null || dbOpenHelper == null) {
            dbOpenHelper = new DbOpenHelper(getTApplication());
        }
        return dbOpenHelper;
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
