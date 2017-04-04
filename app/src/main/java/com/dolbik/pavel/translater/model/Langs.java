package com.dolbik.pavel.translater.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Langs {

    @SerializedName("dirs")
    private List<String> dirs;

    public List<String> getDirs() { return dirs; }

    public void setDirs(List<String> dirs) { this.dirs = dirs; }
}
