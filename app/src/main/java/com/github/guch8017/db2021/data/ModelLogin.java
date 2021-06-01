package com.github.guch8017.db2021.data;

public class ModelLogin extends Model {
    public final String user_id;
    public final String passwd;
    public ModelLogin(String username, String password){
        user_id = username;
        passwd = password;
    }
}
