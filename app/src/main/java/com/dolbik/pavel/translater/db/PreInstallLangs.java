package com.dolbik.pavel.translater.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.dolbik.pavel.translater.model.Language;
import com.dolbik.pavel.translater.utils.Constants;

import net.grandcentrix.tray.AppPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Locale;

/** При первом запуске приложения, заполняем БД значениями из init_langs.json. <br>
 *  When the application is launched for the first time, we fill the database with the values from init_langs.json*/
public class PreInstallLangs implements DbContract {

    private Context        context;
    private AppPreferences pref;
    private DatabaseHelper dbHelper;


    public PreInstallLangs(Context context, AppPreferences pref, DatabaseHelper dbHelper) {
        this.context  = context;
        this.pref     = pref;
        this.dbHelper = dbHelper;
    }


    /** Получаем направление перевода. <br>
     *  Get the direction of translation. */
    public Pair<Language, Language> checkDirectionTranslate() {
        String from = pref.getString(Constants.DIRC_FROM_CODE, "");
        String to   = pref.getString(Constants.DIRC_TO_CODE, "");

        if (TextUtils.isEmpty(from) || TextUtils.isEmpty(to)) {
            return saveLangsInDB();
        } else {
            try {
                Language fromLang = dbHelper.getLanguageDao().queryBuilder().where()
                        .eq(Langs.LANGS_CODE, from).queryForFirst();
                Language toLang = dbHelper.getLanguageDao().queryBuilder().where()
                        .eq(Langs.LANGS_CODE, to).queryForFirst();
                return new Pair<>(fromLang, toLang);
            } catch (SQLException e) {
                e.printStackTrace();
                String fromName = pref.getString(Constants.DIRC_FROM_NAME, "");
                String toName   = pref.getString(Constants.DIRC_TO_NAME,   "");
                return new Pair<>(new Language(from, fromName), new Language(to, toName));
            }
        }
    }


    /** Заполняем БД значениями из init_langs.json. <br>
     *  Fill the database with the values from init_langs.json. */
    private Pair<Language, Language> saveLangsInDB() {
        long start = System.currentTimeMillis();

        Pair<Language, Language> pair = getPairLanguage();
        JSONObject jsonObj = parseJson();
        if (jsonObj == null) {
            return pair;
        } else {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String insert = "INSERT OR REPLACE INTO " + LANGS + "( " +
                    Langs.LANGS_CODE + ", " + Langs.LANGS_NAME +
                    " ) VALUES (?, ?)";
            SQLiteStatement insertStatement = db.compileStatement(insert);

            db.beginTransaction();
            try {
                Iterator<?> keys = jsonObj.keys();
                while (keys.hasNext()) {
                    String key  = String.valueOf(keys.next());
                    String name = String.valueOf(jsonObj.get(key));
                    insertStatement.clearBindings();
                    insertStatement.bindString(1, key);
                    insertStatement.bindString(2, name);
                    insertStatement.executeInsert();

                    if (pair.first.getCode().equals(key))  {
                        pair.first.setName(name);
                        pref.put(Constants.DIRC_FROM_CODE, key);
                        pref.put(Constants.DIRC_FROM_NAME, name);
                    }

                    if (pair.second.getCode().equals(key)) {
                        pair.second.setName(name);
                        pref.put(Constants.DIRC_TO_CODE, key);
                        pref.put(Constants.DIRC_TO_NAME, name);
                    }
                }

                db.setTransactionSuccessful();

                Language from = dbHelper.getLanguageDao().queryBuilder().where()
                        .eq(Langs.LANGS_CODE, pair.first.getCode()).queryForFirst();
                Language to = dbHelper.getLanguageDao().queryBuilder().where()
                        .eq(Langs.LANGS_CODE, pair.second.getCode()).queryForFirst();
                pair = new Pair<>(from, to);

            } catch (JSONException | SQLException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }

            Log.d("Pasha", "Pre install langs "+(System.currentTimeMillis() - start));
            return pair;
        }
    }


    /** Формируем JsonObject из init_langs.json файла. <br>
     *  Create JsonObject from init_langs.json file. */
    private JSONObject parseJson() {
        try {
            InputStream is = context.getAssets().open("init_langs.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);
            return obj.getJSONObject("langs");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /** Формируем пару со значениями по умолчанию. <br>
     *  Create a pair with default values. */
    private Pair<Language, Language> getPairLanguage() {
        Language from = new Language(Locale.getDefault().getLanguage(), "");
        Language to   = new Language("en", "");
        if (from.getCode().equals(to.getCode())) {
            to.setCode("ru");
        }
        return new Pair<>(from, to);
    }
}
