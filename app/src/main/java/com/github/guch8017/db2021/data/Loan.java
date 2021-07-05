package com.github.guch8017.db2021.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Loan implements Serializable {

    public static class PayHistory implements Serializable {
        public int id;
        public String date;
        public double fund;
    }

    public String loan_id;
    public double total;
    public String branch;
    public String create_date;
    public List<String> customers;
    public List<PayHistory> paid_history;
    public String getStatus(){
        if(paid_history == null){
            return "未发放";
        }
        if(paid_history.size() == 0){
            return "未发放";
        }else {
            double paid = 0;
            for(PayHistory history: paid_history){
                paid += history.fund;
            }
            if(Math.abs(total - paid) < 0.001){
                return "发放完成";
            }else{
                return "发放中";
            }
        }
    }

    public Loan(){
        customers = new ArrayList<>();
        paid_history = new ArrayList<>();
    }
}
