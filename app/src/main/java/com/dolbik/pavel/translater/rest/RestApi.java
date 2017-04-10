package com.dolbik.pavel.translater.rest;


import com.dolbik.pavel.translater.model.Translate;
import com.google.gson.JsonElement;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Single;

public interface RestApi {


    /** Получаем список поддерживаемых языков. <br>
     *  Get the list of supported languages. */
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("api/v1.5/tr.json/getLangs")
    Single<JsonElement> getAllLangs(@Field("ui") String ui);


    /** Получаем перевод текста. <br>
     *  Get the translation of the text.*/
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("api/v1.5/tr.json/translate")
    Single<Translate> getTranslate(
            @Field("text") String text,
            @Field("lang") String lang);
}
