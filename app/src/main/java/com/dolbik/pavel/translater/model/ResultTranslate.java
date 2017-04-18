package com.dolbik.pavel.translater.model;

public class ResultTranslate {

    private Translate translate;
    private History   history;


    public ResultTranslate(Translate translate, History history) {
        this.translate = translate;
        this.history   = history;
    }


    public Translate getTranslate() { return translate; }
    public History getHistory()     { return history; }

    public void setHistory(History history) { this.history = history; }
}
