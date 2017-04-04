package com.dolbik.pavel.translater.utils;


import com.dolbik.pavel.translater.model.Langs;

import rx.Single;

public interface Repository {

    /** Получаем список поддерживаемых языков. <br>
     *  Get the list of supported languages. */
    Single<Langs> getAllLangs(String ui);

}
