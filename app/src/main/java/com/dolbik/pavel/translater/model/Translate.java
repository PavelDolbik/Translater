package com.dolbik.pavel.translater.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Translate {

    @SerializedName("code")
    private int code;

    @SerializedName("lang")
    private String lang;

    @SerializedName("text")
    private List<String> test;

    public Translate() {}

    public int          getCode() { return code; }
    public String       getLang() { return lang; }
    public List<String> getTest() { return test; }

    public void setCode(int code)          { this.code = code; }
    public void setLang(String lang)       { this.lang = lang; }
    public void setTest(List<String> test) { this.test = test; }
}
