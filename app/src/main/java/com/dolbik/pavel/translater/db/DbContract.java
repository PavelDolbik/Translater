package com.dolbik.pavel.translater.db;

public interface DbContract {


    String LANGS = "langs";
    interface Langs {
        String LANGS_ID   = "langs_id";
        String LANGS_CODE = "langs_code";
        String LANGS_NAME = "langs_name";
    }


    String HISTORY = "history";
    interface History {
        String ID           = "id";
        String TEXT         = "text";
        String TRANSLATE    = "translate";
        String DIRECTION    = "direction";
        String IS_HISTORY   = "is_history";
        String IS_FAVORITE  = "is_favorite";
        String FROM_LANG_ID = "from_lang_id";
        String TO_LANG_ID   = "to_lang_id";
    }


}
