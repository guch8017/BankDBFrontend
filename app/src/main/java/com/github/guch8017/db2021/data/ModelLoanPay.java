package com.github.guch8017.db2021.data;

public class ModelLoanPay extends Model {
    public String loan_id;
    public double fund;

    public ModelLoanPay(String id, double f){
        loan_id = id;
        fund = f;
    }
}
