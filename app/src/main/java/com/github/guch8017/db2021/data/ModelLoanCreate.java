package com.github.guch8017.db2021.data;

import java.util.List;

public class ModelLoanCreate extends Model {
    public List<String> user_list;
    public double total_fund;
    public String sub_branch;

    public ModelLoanCreate(List<String> usr_l, double f, String branch){
        user_list = usr_l;
        total_fund = f;
        sub_branch = branch;
    }

}
