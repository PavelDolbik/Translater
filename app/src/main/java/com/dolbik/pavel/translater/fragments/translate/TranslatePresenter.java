package com.dolbik.pavel.translater.fragments.translate;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.TApplication;
import com.dolbik.pavel.translater.model.Langs;
import com.dolbik.pavel.translater.utils.DataRepository;

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

        new DataRepository().getAllLangs(Locale.getDefault().getLanguage())
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
                });

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
