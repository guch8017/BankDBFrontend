package com.github.guch8017.db2021.data;

public class ModelCustomer extends Model {
    public String identifier_id;
    public String name;
    public String phone;
    public String address;
    public String s_name;
    public String s_phone;
    public String s_email;
    public String s_rel;

    public ModelCustomer(Customer c){
        identifier_id = c.identifier_id;
        name = c.name;
        phone = c.phone;
        address = c.address;
        s_name = c.s_name;
        s_email = c.s_email;
        s_phone = c.s_phone;
        s_rel = c.s_rel;
    }
}
