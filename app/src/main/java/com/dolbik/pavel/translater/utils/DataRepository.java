package com.dolbik.pavel.translater.utils;


import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.model.Langs;
import com.dolbik.pavel.translater.rest.RestApi;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class DataRepository implements Repository {

    private TApplication application;
    private RestApi      restApi;


    @Override
    public Single<Langs> getAllLangs(String ui) {
        return getRestApi().getAllLangs(ui)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private TApplication getTApplication() {
        if (application == null) {
            application = TApplication.getInstance();
        }
        return application;
    }


    private RestApi getRestApi() {
        if (application == null || restApi == null) {
            restApi = getTApplication().getRestApi();
        }
        return restApi;
    }

}
