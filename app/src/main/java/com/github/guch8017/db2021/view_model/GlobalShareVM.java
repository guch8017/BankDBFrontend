package com.github.guch8017.db2021.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.guch8017.db2021.data.Account;
import com.github.guch8017.db2021.data.Customer;
import com.github.guch8017.db2021.data.Loan;

import java.util.ArrayList;
import java.util.List;

public class GlobalShareVM extends ViewModel {
    private final MutableLiveData<String> string = new MutableLiveData<>();
    private final MutableLiveData<List<String>> stringList = new MutableLiveData<>();

    private final MutableLiveData<Customer> customer = new MutableLiveData<>();
    private final MutableLiveData<Account> account = new MutableLiveData<>();

    private final MutableLiveData<Boolean> unsavedChanges = new MutableLiveData<>(false);
    private final MutableLiveData<Loan> loan = new MutableLiveData<>();


    public void setString(String s){
        string.setValue(s);
    }

    public String getString(){
        return string.getValue();
    }

    public List<String> getStringList(){
        return stringList.getValue();
    }

    public void setStringList(List<String> data){
        stringList.setValue(data);
    }

    public Customer getCustomer(){
        return customer.getValue();
    }

    public void setCustomer(Customer customer){
        this.customer.setValue(customer);
    }

    public Account getAccount(){
        return account.getValue();
    }

    public void setAccount(Account account){
        this.account.setValue(account);
    }


    public boolean hasUnsavedChanges(){
        boolean result = unsavedChanges.getValue();
        unsavedChanges.setValue(false);
        return result;
    }

    public void setChanges(boolean state){
        unsavedChanges.setValue(state);
    }

    public void setLoan(Loan loan){
        this.loan.setValue(loan);
    }

    public Loan getLoan(){
        return loan.getValue();
    }
}
