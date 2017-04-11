package com.dolbik.pavel.translater.db;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Pair;

import com.dolbik.pavel.translater.model.Language;
import com.dolbik.pavel.translater.utils.Constants;

import net.grandcentrix.tray.AppPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Locale;

/** Обновляем языки в БД в соответствии с текущей локалью. <br>
 *  Update the languages in the DB according to the current locale. */
public class UpdateAllLangs implements DbContract {

    private AppPreferences pref;
    private DatabaseHelper dbHelper;


    public UpdateAllLangs(AppPreferences pref, DatabaseHelper dbHelper) {
        this.pref     = pref;
        this.dbHelper = dbHelper;
    }


    public Pair<Language, Language> update(String json) {
        String fromCode = pref.getString(Constants.DIRC_FROM_CODE, "");
        String fromName = pref.getString(Constants.DIRC_FROM_NAME, "");
        String toCode   = pref.getString(Constants.DIRC_TO_CODE,   "");
        String toName   = pref.getString(Constants.DIRC_TO_NAME,   "");

        Pair<Language, Language> pair = new Pair<>(new Language(fromCode, fromName), new Language(toCode, toName));
        if (TextUtils.isEmpty(json)) {return pair;}
        return updateInDb(json, pair);
    }


    private Pair<Language, Language> updateInDb(String json, Pair<Language, Language> pair) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            pref.put(Constants.CURRENT_LOCATION, Locale.getDefault().getLanguage());
            JSONObject obj = new JSONObject(json);
            JSONObject parseJson = obj.getJSONObject("langs");

            String update = "UPDATE " + LANGS + " SET " + Langs.LANGS_NAME + "=? WHERE " + Langs.LANGS_CODE + " =? ";
            SQLiteStatement updateStatement = db.compileStatement(update);

            String insert = "INSERT OR REPLACE INTO " + LANGS + "( " +
                    Langs.LANGS_CODE + ", " + Langs.LANGS_NAME + " ) VALUES (?, ?)";
            SQLiteStatement insertStatement = db.compileStatement(insert);

            db.beginTransaction();
            Iterator<?> keys = parseJson.keys();
            while (keys.hasNext()) {
                String key  = String.valueOf(keys.next());
                String name = String.valueOf(parseJson.get(key));

                updateStatement.clearBindings();
                updateStatement.bindString(1, name);
                updateStatement.bindString(2, key);
                int count = updateStatement.executeUpdateDelete();

                if (count == 0) {
                    insertStatement.clearBindings();
                    insertStatement.bindString(1, key);
                    insertStatement.bindString(2, name);
                    insertStatement.executeInsert();
                }

                if (pair.first.getCode().equals(key))  {
                    pair.first.setName(name);
                    pref.put(Constants.DIRC_FROM_NAME, name);
                }

                if (pair.second.getCode().equals(key)) {
                    pair.second.setName(name);
                    pref.put(Constants.DIRC_TO_NAME, name);
                }
            }

            db.setTransactionSuccessful();
            return pair;
        } catch (JSONException e) {
            e.printStackTrace();
            return pair;
        } finally {
            db.endTransaction();
        }
    }

}
