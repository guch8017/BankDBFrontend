package com.github.guch8017.db2021;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.github.guch8017.db2021.data.Model;
import com.github.guch8017.db2021.data.ModelLoginResponse;
import com.github.guch8017.db2021.data.ModelResponseBase;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtil<TResponse extends ModelResponseBase> {
    public interface INetworkCallback<T>{
        void onResponse(T model);
        void onNoLogin();
        void onFailure(Exception e);
    }



    private static SharedPreferences mPreference;
    private final OkHttpClient mClient = new OkHttpClient();

    public static void init(Context context){
        mPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void doGetRequest(String url, INetworkCallback<TResponse> callback, Class<TResponse> clazz){
        final Request req = new Request.Builder()
                .url(url)
                .get()
                .addHeader(Static.HEADER_USER, mPreference.getString(Static.HEADER_USER, ""))
                .addHeader(Static.HEADER_SESS, mPreference.getString(Static.HEADER_SESS, ""))
                .build();
        mClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Looper.prepare();
                try {
                    String payload = response.body().string();
                    Log.d("NetworkUtil", payload);
                    TResponse resp = new Gson().fromJson(payload, clazz);
                    if(resp.code == Static.ERR_NO_LOGIN){
                        callback.onNoLogin();
                    }
                    else {
                        callback.onResponse(resp);
                    }
                } catch (Exception e){
                    callback.onFailure(e);
                } finally {
                    Looper.loop();
                }

            }
        });
    }

    public void doRequestWithSession(String url, Model payload, INetworkCallback<TResponse> callback, Class<TResponse> clazz){
        final Request req = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse(Static.JSON_MEDIA), payload.toJson()))
                .addHeader(Static.HEADER_USER, mPreference.getString(Static.HEADER_USER, ""))
                .addHeader(Static.HEADER_SESS, mPreference.getString(Static.HEADER_SESS, ""))
                .build();
        mClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                callback.onFailure(e);
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Looper.prepare();
                try {
                    String payload = response.body().string();
                    Log.d("NetworkUtil", payload);
                    TResponse resp = new Gson().fromJson(payload, clazz);
                    if(resp.code == Static.ERR_NO_LOGIN){
                        callback.onNoLogin();
                    }
                    else {
                        if(resp.code != 0){
                            callback.onFailure(new RuntimeException(resp.msg));
                        } else {
                            callback.onResponse(resp);
                        }
                    }
                } catch (RuntimeException e){
                    callback.onFailure(e);
                } finally {
                    Looper.loop();
                }


            }
        });
    }

}
