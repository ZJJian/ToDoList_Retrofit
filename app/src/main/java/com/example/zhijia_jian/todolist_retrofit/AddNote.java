package com.example.zhijia_jian.todolist_retrofit;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddNote extends AppCompatActivity {
    private EditText editText;
    private EditText texteditText;
    private Button addNoteButton;
    private String token;
    private Long noteId;
    private static final int EDIT=2;

    private NoteApi mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        addNoteButton = (Button)findViewById(R.id.buttonAdd);
        editText = (EditText) findViewById(R.id.titleET);
        texteditText = (EditText) findViewById(R.id.textET);

        Bundle bun = this.getIntent().getExtras();
        token=bun.getString("token");
        Log.d("app", token);
        noteId=bun.getLong("noteId");
        if(noteId!=-1) {
            editText.setText(bun.getString("title"));
            texteditText.setText(bun.getString("content"));
            addNoteButton.setText("Update");
        }


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
    public void onAddButtonClick(View view) {
        String noteText = editText.getText().toString();
        String textnoteText = texteditText.getText().toString();

        Note note = new Note();
        note.setTitle(noteText);
        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment =(noteId==-1)?"Added on " + df.format(new Date()) : "Edited on " + df.format(new Date());
        note.setComment(comment);
        note.setDate(new Date());
        note.setText(textnoteText);

        if(noteId==-1)
            AddOneNote(note);
        else
            UpdateNote(note);

    }
    private void UpdateNote(Note note)
    {
        Call<String> call = mService.update(noteId,note);

        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.d("APP", "UpdateNoteonResponse: "+response.body().toString());
                Intent intent = new Intent();
                Bundle bun=new Bundle();
                bun.putString("token",token);
                intent.putExtras(bun);
                setResult(RESULT_OK,intent);
                finish();


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("APP", "UpdateNoteonFailure: ");
            }
        });
    }
    private void AddOneNote(Note note)
    {
        Call<String> call = mService.post(note);

        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.d("APP", "AddOneNoteResponse: "+response.body().toString());

                Intent intent = new Intent();
                Bundle bun=new Bundle();
                bun.putString("token",token);
                intent.putExtras(bun);
                setResult(RESULT_OK,intent);
                finish();


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("APP", "AddOneNoteFailure: ");
            }
        });
    }
}

