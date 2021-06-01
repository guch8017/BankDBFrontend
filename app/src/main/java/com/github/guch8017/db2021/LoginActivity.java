package com.github.guch8017.db2021;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.github.guch8017.db2021.data.ModelLogin;
import com.github.guch8017.db2021.data.ModelLoginResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText mPassword;
    private EditText mUsername;
    private Button mLoginBtn;
    private SharedPreferences mPreference;
    private OkHttpClient mClient = new OkHttpClient();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_login);
        mPassword = findViewById(R.id.et_pwd);
        mUsername = findViewById(R.id.et_uid);
        mLoginBtn = findViewById(R.id.btn_login);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View view = View.inflate(LoginActivity.this, R.layout.dialog_search_customer, null);
                builder.setView(view);
                builder.create().show();
                 */
                mLoginBtn.setClickable(false);
                doLogin();
            }
        });
    }

    private void doLogin() {
        final String user_id = mUsername.getText().toString();
        final String password = mPassword.getText().toString();
        ModelLogin d = new ModelLogin(user_id, password);
        final Request req = new Request.Builder()
                .url(Static.LOGIN)
                .post(RequestBody.create(MediaType.parse(Static.JSON_MEDIA), d.toJson()))//new Gson().toJson(d)))
                .build();
        mClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(LoginActivity.this, "登录失败\n" + e, Toast.LENGTH_LONG).show();
                mLoginBtn.setClickable(true);
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ModelLoginResponse resp = new Gson().fromJson(response.body().string(), ModelLoginResponse.class);
                final SharedPreferences.Editor editor = mPreference.edit();
                Looper.prepare();
                if(!resp.success){
                    editor.putBoolean("login", false);
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "登录失败\n" + resp.msg, Toast.LENGTH_LONG).show();
                }else {
                    editor.putBoolean("login", true);
                    editor.putString("user_id", user_id);
                    editor.putString("session", resp.data.session);
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                }
                mLoginBtn.setClickable(true);
                Looper.loop();
            }
        });
    }


}
