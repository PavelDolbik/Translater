package com.dolbik.pavel.translater.fragments.translate;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface TranslateView extends MvpView {


    /** Скрываем progressBar и показываем направление перевода. <br>
     *  Hide progressBar and show the direction of the translation. */
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showToolbarView();


    /** Обновляем значения направления перевода. <br>
     *  Update the values of the translation direction. */
    @StateStrategyType(AddToEndSingleStrategy.class)
    void updateTranslateDirection(String from, String to);


    /** Показываем SnakeBar. <br>
     *  Show SnakeBar.*/
    @StateStrategyType(OneExecutionStateStrategy.class)
    void showSnakeBar(String message);


    /** Показываем представление для очистки поля ввода. <br>
     *  Show the view to clear the input field. */
    @StateStrategyType(OneExecutionStateStrategy.class)
    void showCleanBtn();


    /** Скрываем представление для очистки поля ввода. <br>
     *  Hide the view to clear the input field. */
    @StateStrategyType(OneExecutionStateStrategy.class)
    void hideCleanBtn();


    /** Показываем/Скрываем представление для добавления в Избранное. <br>
     *  Show/Hide view to add to Favorites. */
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showHideFavoriteBtn(boolean flag, boolean isFavorite);


    /** Отображаем определенную разметку. <br>
     *  display a certain layout. */
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showViewStub(@TranslateFragmentState.State int state, String translate);


    /** Устанавливаем текст, который будет переведен. <br>
     *  Set the text to be translated. */
    @StateStrategyType(OneExecutionStateStrategy.class)
    void setTextForTranslate(String text);

}
