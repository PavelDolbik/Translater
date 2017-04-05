package com.dolbik.pavel.translater.db;

public interface DbContract {

    String LANGS = "langs";
    interface Langs {
        String LANGS_ID   = "langs_id";
        String LANGS_CODE = "langs_code";
        String LANGS_NAME = "langs_name";
    }
}
