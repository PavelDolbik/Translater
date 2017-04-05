package com.dolbik.pavel.translater.model;


public class Language {

    private String code;
    private String name;

    public Language() {}

    public Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }

}
