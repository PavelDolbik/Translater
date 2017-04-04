package com.dolbik.pavel.translater.fragments.translate;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.model.Langs;
import com.dolbik.pavel.translater.utils.DataRepository;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;

import rx.SingleSubscriber;


@InjectViewState
public class TranslatePresenter extends MvpPresenter<TranslateView> {

    private TApplication application;


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        Log.d("Pasha", "onFirstViewAttach "+ Locale.getDefault().getLanguage());
        Log.d("Pasha", "Is connected "+getApplication().isConnected());

        loadInitLangsJson();

        /*new DataRepository().getAllLangs(Locale.getDefault().getLanguage())
                .subscribe(new SingleSubscriber<Langs>() {
                    @Override
                    public void onSuccess(Langs value) {
                        for (String s : value.getDirs()) {
                            Log.d("Pasha", ""+s);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }
                });*/

    }


    private void loadInitLangsJson() {
        try {
            InputStream is = getApplication().getAssets().open("init_langs.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONObject obj = new JSONObject(json);
            JSONObject langs = obj.getJSONObject("langs");

            Iterator<?> keys = langs.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Log.d("Pasha", ""+key+" "+langs.get(key));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
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
        Log.d("Pasha", "onDestroy");
    }
}
