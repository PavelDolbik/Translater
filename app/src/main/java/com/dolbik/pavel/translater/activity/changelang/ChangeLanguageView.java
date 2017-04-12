package com.dolbik.pavel.translater.activity.changelang;


import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.dolbik.pavel.translater.model.Language;

import java.util.List;

public interface ChangeLanguageView extends MvpView {


    /** Показываем SnakeBar. <br>
     *  Show SnakeBar.*/
    @StateStrategyType(OneExecutionStateStrategy.class)
    void showSnakeBar(String message);


    /** Скрываем progressBar. <br>
     *  Hide progressBar.*/
    @StateStrategyType(AddToEndSingleStrategy.class)
    void hideProgress();


    /** Передаем данные в адаптер. <br>
     *  Send the data to the adapter. */
    void setData(List<Language> data);

}
