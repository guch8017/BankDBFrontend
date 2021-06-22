package com.github.guch8017.db2021.ui;

public class InfoItem {
    public int tag;
    public String data;
    public boolean editable;

    InfoItem(int tag, String data){
        this.data = data;
        this.tag = tag;
        this.editable = true;
    }

    InfoItem(int tag, String data, boolean editable){
        this.data = data;
        this.tag = tag;
        this.editable = editable;
    }
}
