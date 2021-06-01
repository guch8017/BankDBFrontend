package com.github.guch8017.db2021.data;

import com.google.gson.Gson;

public class Model {
    protected static final Gson gsonInstance = new Gson();
    public String toJson(){
        return gsonInstance.toJson(this);
    }
}
