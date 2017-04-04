package com.dolbik.pavel.translater.activity;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;


interface MainActivityView extends MvpView {


    @StateStrategyType(OneExecutionStateStrategy.class)
    void selectFragment(int menuItemId);

}
