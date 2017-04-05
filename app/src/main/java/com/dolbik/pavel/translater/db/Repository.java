package com.dolbik.pavel.translater.db;


import android.util.Pair;

import com.dolbik.pavel.translater.model.Language;
import com.google.gson.JsonElement;

import rx.Observable;
import rx.Single;

public interface Repository {

    /** При первом запуске, заполняем БД из init_langs.json <br>
     * At the first start, we fill the database from init_langs.json */
    Observable<Pair<Language, Language>> preInstallLangs();


    /** Получаем список поддерживаемых языков. <br>
     *  Get the list of supported languages. */
    Single<JsonElement> getAllLangs(String ui);

}
