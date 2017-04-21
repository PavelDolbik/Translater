package com.dolbik.pavel.translater.fragments.note;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;


public interface NoteView extends MvpView {

    /** Показать/Скрыть меню удаления. <br>
     *  Show/Hide menu remove item. S*/
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showHideRemoveItem(boolean flag);

}
