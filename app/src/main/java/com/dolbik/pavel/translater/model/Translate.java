package com.dolbik.pavel.translater.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Translate {

    @SerializedName("code")
    private int code;

    @SerializedName("lang")
    private String lang;

    @SerializedName("text")
    private List<String> text;

    public Translate() {}

    public int          getCode() { return code; }
    public String       getLang() { return lang; }
    public List<String> getText() { return text; }

    public void setCode(int code)          { this.code = code; }
    public void setLang(String lang)       { this.lang = lang; }
    public void setText(List<String> text) { this.text = text; }
}
