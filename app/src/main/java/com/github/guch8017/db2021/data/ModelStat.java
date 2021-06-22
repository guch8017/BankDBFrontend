package com.github.guch8017.db2021.data;

public class ModelStat extends Model{
    public String date_from;
    public String date_to;
    public ModelStat(String from, String to){
        date_from = from;
        date_to = to;
    }
}
