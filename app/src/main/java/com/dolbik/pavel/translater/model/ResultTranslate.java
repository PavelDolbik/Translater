package com.dolbik.pavel.translater.model;

/**
 * Created by pavel.d on 13-Apr-17.
 */

public class ResultTranslate {

    private Translate translate;
    private History   history;


    public ResultTranslate(Translate translate, History history) {
        this.translate = translate;
        this.history   = history;
    }


    public Translate getTranslate() { return translate; }
    public History getHistory()     { return history; }
}
