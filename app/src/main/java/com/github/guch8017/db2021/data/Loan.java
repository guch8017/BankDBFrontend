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

    public Loan(){
        customers = new ArrayList<>();
        paid_history = new ArrayList<>();
    }
}
