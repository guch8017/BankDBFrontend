package com.github.guch8017.db2021.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ModelAccount extends Model {
    public String account_id;
    public double refund;
    public String sub_branch;
    public int type;
    public double rate;
    public String finance;
    public double overdraft;
    public List<String> holder;

    public ModelAccount(){
        holder = new ArrayList<>();
        refund = 0;
        rate = 0;
        type = 0;
        overdraft = 0;
    }

    public ModelAccount(@NonNull Account acc){
        account_id = acc.account_id;
        refund = acc.refund;
        sub_branch = acc.sub_branch;
        type = acc.type;
        rate = acc.rate;
        finance = acc.finance;
        overdraft = acc.overdraft;
        holder = new ArrayList<>(acc.holder);
    }
}
