package com.github.guch8017.db2021;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;

import com.github.guch8017.db2021.data.ModelLogin;
import com.github.guch8017.db2021.data.ModelLoginResponse;
import com.github.guch8017.db2021.ui.FragmentAccountList;
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

public class LoginFragment extends Fragment {
    private EditText mPassword;
    private EditText mUsername;
    private Button mLoginBtn;
    private SharedPreferences mPreference;
    private OkHttpClient mClient = new OkHttpClient();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreference = PreferenceManager.getDefaultSharedPreferences(getContext());
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        mPassword = view.findViewById(R.id.et_pwd);
        mUsername = view.findViewById(R.id.et_uid);
        mLoginBtn = view.findViewById(R.id.btn_login);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*

                 */
                mLoginBtn.setClickable(false);
                doLogin();
            }
        });
        return view;
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
                Toast.makeText(LoginFragment.this.getContext(), "登录失败\n" + e, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(LoginFragment.this.getContext(), "登录失败\n" + resp.msg, Toast.LENGTH_LONG).show();
                }else {
                    editor.putBoolean("login", true);
                    editor.putString(Static.HEADER_USER, user_id);
                    editor.putString(Static.HEADER_SESS, resp.data.session);
                    editor.apply();
                    Toast.makeText(LoginFragment.this.getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                    getActivity().runOnUiThread(() -> NavHostFragment.findNavController(LoginFragment.this).navigateUp());

                }
                mLoginBtn.setClickable(true);
                Looper.loop();
            }
        });
    }


}
