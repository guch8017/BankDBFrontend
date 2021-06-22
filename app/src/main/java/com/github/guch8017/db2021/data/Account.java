package com.github.guch8017.db2021.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable {
    public String account_id;
    public double refund;
    public String open_date;
    public String sub_branch;
    public String recent_visit;
    public int type;
    public double rate;
    public String finance;
    public double overdraft;
    public List<String> holder;

    public Account(){
        holder = new ArrayList<>();
        refund = 0;
        rate = 0;
        type = 0;
        overdraft = 0;
    }

    public void reset(Account account){
        account_id = account.account_id;
        refund = account.refund;
        open_date = account.open_date;
        sub_branch = account.sub_branch;
        recent_visit = account.recent_visit;
        type = account.type;
        rate = account.rate;
        finance = account.finance;
        overdraft = account.overdraft;
        holder = account.holder;
    }

}
