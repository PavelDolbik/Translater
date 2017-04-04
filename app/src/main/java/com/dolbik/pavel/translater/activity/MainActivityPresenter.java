package com.dolbik.pavel.translater.activity;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dolbik.pavel.translater.R;


@InjectViewState
public class MainActivityPresenter extends MvpPresenter<MainActivityView> {


    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        // При первом запуске отображаем TranslateFragment.
        // At the first launch, we display the TranslateFragment.
        getViewState().selectFragment(R.id.navigation_translate);
    }

}
