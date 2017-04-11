package com.dolbik.pavel.translater.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;


public class DatabaseManager {

    private static volatile DatabaseManager instance;
    private DatabaseHelper  helper;


    private DatabaseManager() {}


    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }


    public DatabaseHelper getHelper(Context context) {
        if(helper == null){
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return helper;
    }

}
