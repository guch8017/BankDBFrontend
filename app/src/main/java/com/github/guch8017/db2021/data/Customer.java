package com.github.guch8017.db2021.data;

import java.io.Serializable;

public class Customer implements Serializable {
    public String identifier_id;
    public String name;
    public String phone;
    public String address;
    public String s_name;
    public String s_phone;
    public String s_email;
    public String s_rel;

    public Customer(
            String id,
            String name,
            String phone,
            String address,
            String s_name,
            String s_phone,
            String s_email,
            String s_rel
    ){
        this.identifier_id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.s_name = s_name;
        this.s_phone = s_phone;
        this.s_email = s_email;
        this.s_rel = s_rel;
    }

    public Customer(){}
}
