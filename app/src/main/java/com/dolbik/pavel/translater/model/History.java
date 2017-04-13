package com.dolbik.pavel.translater.model;


import com.dolbik.pavel.translater.db.DbContract;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DbContract.HISTORY)
public class History implements DbContract {

    @DatabaseField(id = true, columnName = History.ID)
    private int id;

    @DatabaseField(columnName = History.TEXT)
    private String text;

    @DatabaseField(columnName = History.TRANSLATE)
    private String translate;

    @DatabaseField(columnName = History.DIRECTION)
    private String direction;

    @DatabaseField(columnName = History.IS_FAVORITE)
    private boolean isFavorite;


    public History() {}

    public int    getId()        { return id; }
    public String getText()      { return text; }
    public String getTranslate() { return translate; }
    public String getDirection() { return direction; }
    public boolean isFavorite()  { return isFavorite; }

    public void setId(int id)                  { this.id = id; }
    public void setText(String text)           { this.text = text; }
    public void setTranslate(String translate) { this.translate = translate; }
    public void setDirection(String direction) { this.direction = direction; }
    public void setFavorite(boolean favorite)  { isFavorite = favorite; }
}
