package com.github.guch8017.db2021.ui;

public class ViewType<T> {
    public T data;
    public int layoutId;

    public ViewType(T dt, int id){
        data = dt;
        layoutId = id;
    }
}
