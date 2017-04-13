package com.dolbik.pavel.translater.db;


import android.util.Pair;

import com.dolbik.pavel.translater.model.History;
import com.dolbik.pavel.translater.model.Language;
import com.dolbik.pavel.translater.model.ResultTranslate;
import com.dolbik.pavel.translater.model.Translate;

import java.util.List;

import rx.Observable;
import rx.Single;

public interface Repository {


    /** При первом запуске, заполняем БД из init_langs.json <br>
     * At the first start, we fill the database from init_langs.json */
    Observable<Pair<Language, Language>> preInstallLangs();


    /** Получаем перевод всех доступных языков в соответствии с текущей локалью. <br>
     *  Get the translation of all available languages according to the current locale. */
    Observable<Pair<Language, Language>> getAllLangs();


    /** Получаем перевод текста. <br>
     *  Get the translation of the text.*/
    Single<Translate> getTranslate(String text, String lang);


    /** Получаем список доступных языков из БД. <br>
     *  Get a list of available languages from the database. */
    Single<List<Language>> getLangsFromDB();


    /** Ищим перевод в БД. <br>
     *  Looking for translation into DB. */
    Single<History> getHistoryEntity(String text, String direction);


    /** Получаем результат перевода с сервера и БД. <br>
     *  Get the result of translation from the server and the database. */
    Observable<ResultTranslate> getResultTranslate(String text, String lang);


}
