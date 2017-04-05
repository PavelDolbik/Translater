package com.dolbik.pavel.translater.fragments.translate;

import android.util.Log;
import android.util.Pair;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.db.DataRepository;
import com.dolbik.pavel.translater.db.Repository;
import com.dolbik.pavel.translater.model.Language;

import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


@InjectViewState
public class TranslatePresenter extends MvpPresenter<TranslateView> {


    private Repository            repository;
    private CompositeSubscription compositeSbs;


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        repository   = new DataRepository();
        compositeSbs = new CompositeSubscription();

        prepareInstallLangsFromJson();
    }


    private void prepareInstallLangsFromJson() {
        Subscription sbs = repository.preInstallLangs()
                .subscribe(new Subscriber<Pair<Language, Language>>() {
                    @Override
                    public void onNext(Pair<Language, Language> pair) {
                        Log.d("Pasha", ""+pair.first.getCode()+" "+pair.first.getName()+" "+pair.second.getCode()+" "+pair.second.getName());
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

    /*new DataRepository().getAllLangs(Locale.getDefault().getLanguage())
                .subscribe(new SingleSubscriber<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement value) {
                        Log.d("Pasha", "****************");
                        JsonObject jsonObj = value.getAsJsonObject();
                        String strObj = value.toString();
                        Log.d("Pasha", "strObj -> "+strObj);
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }
                });*/


    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSbs.unsubscribe();
        repository = null;
    }
}
