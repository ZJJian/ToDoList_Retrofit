package com.example.zhijia_jian.todolist_retrofit.Services;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.zhijia_jian.todolist_retrofit.Models.Note;
import com.example.zhijia_jian.todolist_retrofit.Models.Token;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoteClient {

    private static NoteClient instance = null;
    private final NoteApi mService;
    private static String token = "";
    private Context context;
    private SharedPreferences settings;
    private static final String data = "DATA";
    private static final String usernameField = "USERNAME";
    private static final String passwordField = "PASSWORD";
    private static final String tokenField = "TOKEN";

    private NoteClient() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(1000*30, TimeUnit.MILLISECONDS);
        httpClient.writeTimeout(600, TimeUnit.MILLISECONDS);
        httpClient.addInterceptor(new Interceptor() {

            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {

                Request original = chain.request();
                Request.Builder requestBuilder;
                // Request customization: add request headers
                if(!TextUtils.isEmpty(token)) {

                    requestBuilder = original.newBuilder()
                            .header("x-access-token", token); // <-- this is the important line

                } else {

                    requestBuilder = original.newBuilder();

                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        Log.d("APP", "token " + token);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://todolist-token.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        mService = retrofit.create(NoteApi.class);

    }

    public static NoteClient getInstance() {

        if (instance == null) {

            instance = new NoteClient();

        }

        return instance;
    }

    public boolean alreadyLogin() {

        if(TextUtils.isEmpty(readData())) {

            return false;

        } else {

            token = readData();
            return true;

        }

    }

    public void logout() {

        deleteData();
        token = "";
        instance = null;
    }

    public void setContext(Context context) {

        this.context = context;

    }

    public String getUsername() {

        settings = context.getSharedPreferences(data, 0);
        return settings.getString(usernameField, "");

    }

    private String readData(){

        settings = context.getSharedPreferences(data, 0);
        return settings.getString(tokenField, "");

    }

    private void saveData(String token, String username, String password){

        settings = context.getSharedPreferences(data, 0);
        settings.edit()
                .putString(usernameField, username)
                .putString(passwordField, password)
                .putString(tokenField, token)
                .apply();

    }

    private void deleteData() {

        settings = context.getSharedPreferences(data, 0);
        settings.edit().remove(usernameField).apply();
        settings.edit().remove(passwordField).apply();
        settings.edit().remove(tokenField).apply();

    }

    public boolean login(final String username, final String password) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Boolean> callable = new Callable<Boolean>() {

            @Override
            public Boolean call() {

                retrofit2.Call<Token> call = mService.login(username,password);
                try {

                    retrofit2.Response<Token> response = call.execute();
                    if (response.code() == 406 && response.message().equals("Not Acceptable")) {

                        return false;

                    } else {

                        Token t =response.body();
                        saveData(t.getToken(), username, password);
                        token = t.getToken();
                        return true;

                    }

                } catch (Exception ex) {

                    Log.d("app", "call: ex: " + ex.toString());
                    return false;

                }
            }
        };

        Future<Boolean> future = executor.submit(callable);
        executor.shutdown();

        try {

            return future.get();

        } catch (Exception ex) {

            Log.d("app", "login: ex: " + ex.toString());
            return  false;

        }

    }

    public Call<String> register(String username, String password) {

        return mService.register(username, password);

    }

    public Call<List<Note>> getNoteList() {

        return mService.getNoteList();

    }

    public Call<Note> getNote(int id) {

        return mService.getNote(id);

    }

    public Call<String> post(Note note) {

        return mService.post(note);

    }

    public Call<String> update(Long id, Note note) {

        return mService.update(id, note);

    }

    public Call<String> delete(Long id) {

        return mService.delete(id);

    }

}