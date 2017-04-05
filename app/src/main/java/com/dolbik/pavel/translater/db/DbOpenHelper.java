package com.dolbik.pavel.translater.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper implements DbContract {

    private static final String  DB_NAME    = "main.db";
    private static final int     DB_VERSION = 1;


    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ LANGS + "(" +
                Langs.LANGS_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "  +
                Langs.LANGS_CODE + " TEXT UNIQUE NOT NULL, " +
                Langs.LANGS_NAME + " TEXT UNIQUE NOT NULL "  +
                ")");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+ LANGS);
    }

}
