package com.github.guch8017.db2021.data;

public class ModelLoanSearch extends Model {
    public String keyword;
    public int method;

    public ModelLoanSearch(String kw, int m){
        keyword = kw;
        method = m;
    }
}
