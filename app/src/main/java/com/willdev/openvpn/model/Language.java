package com.willdev.openvpn.model;


import com.google.gson.annotations.SerializedName;

public class Language {

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("direction")
    public String direction;
    @SerializedName("json")
    public String wordJson;

    @SerializedName("flag")
    public String flag;

    public Language(int id, String name, String direction, String wordJson, String flag) {
        this.id = id;
        this.name = name;
        this.direction = direction;
        this.wordJson = wordJson;
        this.flag = flag;
    }
}