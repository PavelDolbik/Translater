package com.dolbik.pavel.translater.db;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class HistoryDB implements DbContract {

    private DatabaseHelper dbHelper;

    public HistoryDB(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }


    public void saveInHistory(com.dolbik.pavel.translater.model.History history) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String insert = "INSERT OR REPLACE INTO " + HISTORY + "( " +
                History.TEXT + ", " + History.TRANSLATE + ", " +
                History.DIRECTION + ", " +  History.IS_FAVORITE + " ) VALUES (?, ?, ?, ?)";
        SQLiteStatement insertStatement = db.compileStatement(insert);

        db.beginTransaction();
        try {
            insertStatement.clearBindings();
            insertStatement.bindString(1, history.getText());
            insertStatement.bindString(2, history.getTranslate());
            insertStatement.bindString(3, history.getDirection());
            insertStatement.bindLong(  4, history.isFavorite() ? 1 : 0);
            insertStatement.executeInsert();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Log.d("Pasha", "History entity save in DB");
    }


    public void updateFavorite(com.dolbik.pavel.translater.model.History history) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String update = "UPDATE " + HISTORY + " SET " + History.IS_FAVORITE + "=? WHERE " + History.TEXT + " =? ";
        SQLiteStatement updateStatement = db.compileStatement(update);

        db.beginTransaction();
        try {
            updateStatement.clearBindings();
            updateStatement.bindLong(  1, history.isFavorite() ? 1 : 0);
            updateStatement.bindString(2, history.getText());
            updateStatement.executeUpdateDelete();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
