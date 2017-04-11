package com.dolbik.pavel.translater.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dolbik.pavel.translater.model.Language;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME    = "main.db";
    private static final int    DATABASE_VERSION = 1;

    private Dao<Language, Integer> languageDao = null;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            makeTables(connectionSource);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            removeTables(connectionSource);
            makeTables(connectionSource);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Language, Integer> getLanguageDao() throws java.sql.SQLException {
        if (languageDao == null) { languageDao = getDao(Language.class); }
        return languageDao;
    }


    private synchronized void makeTables(ConnectionSource connectionSource) throws java.sql.SQLException {
        TableUtils.createTable(connectionSource, Language.class);
    }


    private synchronized void removeTables(ConnectionSource connectionSource) throws java.sql.SQLException {
        TableUtils.dropTable(connectionSource, Language.class, true);
    }

}
