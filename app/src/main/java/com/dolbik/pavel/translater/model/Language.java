package com.dolbik.pavel.translater.model;


import com.dolbik.pavel.translater.db.DbContract;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DbContract.LANGS)
public class Language implements DbContract{

    @DatabaseField(id = true, columnName = Langs.LANGS_ID)
    private int id;

    @DatabaseField(dataType = DataType.STRING, columnName = Langs.LANGS_CODE)
    private String code;

    @DatabaseField(dataType = DataType.STRING, columnName = Langs.LANGS_NAME)
    private String name;


    public Language() {}


    public Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public int    getId()   { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }

    public void setId(int id)        { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }

}
