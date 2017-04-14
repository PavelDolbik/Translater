package com.dolbik.pavel.translater.fragments.history;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.dolbik.pavel.translater.model.History;
import com.dolbik.pavel.translater.model.Language;

import java.util.List;

public interface HistoryView extends MvpView {


    /** Показываем/Скрываем прогресс бар. <br>
     *  Show/Hide the progress bar. */
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showHideProgress(boolean flag);


    /** Показываем SnakeBar. <br>
     *  Show SnakeBar.*/
    @StateStrategyType(OneExecutionStateStrategy.class)
    void showSnakeBar(String message);


    /** Передаем данные в адаптер. <br>
     *  Send the data to the adapter. */
    void setData(List<History> data);


}
