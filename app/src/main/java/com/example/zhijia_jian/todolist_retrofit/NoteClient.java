package com.example.zhijia_jian.todolist_retrofit;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoteClient {

    private static NoteClient userService = null;
    private static NoteClient noteService = null;
    private final NoteApi mService;


    public NoteClient(final String token) {
        //這邊的使用情境為全部 API 都對同一個 Server 操作
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(1000*30, TimeUnit.MILLISECONDS);
        httpClient.writeTimeout(600, TimeUnit.MILLISECONDS);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("x-access-token",token); // <-- this is the important line

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        Log.d("APP","token " +token);

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
    public NoteClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(1000*30, TimeUnit.MILLISECONDS);
        httpClient.writeTimeout(600, TimeUnit.MILLISECONDS);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder();
                //        .header("x-access-token",token); // <-- this is the important line

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

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
    public static NoteClient getUserService() {
        if (userService == null) {
            userService = new NoteClient();
        }

        return userService;
    }
    public static NoteClient getNoteService(String token) {
        if (noteService == null) {
            noteService = new NoteClient(token);
        }

        return noteService;
    }

    public void release() {
        noteService=null;
    }

    public Call<Token> Login(String username, String password) {
        return mService.login(username,password);
    }

    public Call<String> Register(String username,String password) {
        return mService.register(username,password);
    }

    public Call<List<Note>> GetNoteList() {
        return mService.getNoteList();
    }

    public Call<Note> GetNote(int id) {
        return mService.getNote(id);
    }

    public Call<String> Post(Note note) {
        return mService.post(note);
    }

    public Call<String> Update(Long id, Note note) {
        return mService.update(id,note);
    }

    public Call<String> Delete(Long id) {
        return mService.delete(id);
    }

}