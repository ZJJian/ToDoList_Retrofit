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
        if(noteId==-1)
            addNote();
        else
            editNote();


//        Intent intent = new Intent();
//        //intent.setClass(AddNote.this , ToDoLists.class);
//        Bundle bun=new Bundle();
//        bun.putString("token",token);
//        intent.putExtras(bun);
//        //startActivityForResult(intent, EDIT);
//        setResult(RESULT_OK,intent);
//        finish();
    }
    private void editNote() {
        String noteText = editText.getText().toString();
        //editText.setText("");
        String textnoteText = texteditText.getText().toString();
        //texteditText.setText("");


        Note note = new Note();
        note.setTitle(noteText);
        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Edited on " + df.format(new Date());
        note.setComment(comment);
        note.setDate(new Date());
        note.setText(textnoteText);

//        Gson gson = new Gson();
//        //將Book物件轉成JSON
//        String json = gson.toJson(note);
        UpdateNote(note);
        //new MyTask().execute(json);

    }
    private void UpdateNote(Note note)
    {
        Call<String> call = mService.update(noteId,note);

        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.d("APP", "UpdateNoteonResponse: "+response.body().toString());
                Intent intent = new Intent();
                //intent.setClass(AddNote.this , ToDoLists.class);
                Bundle bun=new Bundle();
                bun.putString("token",token);
                intent.putExtras(bun);
                //startActivityForResult(intent, EDIT);
                setResult(RESULT_OK,intent);
                finish();


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("APP", "UpdateNoteonFailure: ");
            }
        });
    }


    private void addNote() {
        String noteText = editText.getText().toString();
        //editText.setText("");
        String textnoteText = texteditText.getText().toString();
        //texteditText.setText("");

        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Added on " + df.format(new Date());

        Note note = new Note();
        note.setTitle(noteText);
        note.setComment(comment);
        note.setDate(new Date());
        note.setText(textnoteText);


        Gson gson = new Gson();
//        //將Book物件轉成JSON
        String json = gson.toJson(note);
//        Log.d("app",json);
//
//        noteToServere(json,token);
        AddOneNote(note);
        //noteDao.insert(note);
        //Log.d("DaoExample", "Inserted new note, ID: " + note.getId());

    }
    private void AddOneNote(Note note)
    {
        Call<String> call = mService.post(note);

        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.d("APP", "AddOneNoteResponse: "+response.body().toString());

                Intent intent = new Intent();
                //intent.setClass(AddNote.this , ToDoLists.class);
                Bundle bun=new Bundle();
                bun.putString("token",token);
                intent.putExtras(bun);
                //startActivityForResult(intent, EDIT);
                setResult(RESULT_OK,intent);
                finish();


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("APP", "AddOneNoteFailure: ");
            }
        });
    }
    private void noteToServere(final String json, final String token)
    {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final ExecutorService service = Executors.newFixedThreadPool(10);

        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(1000*30, TimeUnit.MILLISECONDS);
        b.writeTimeout(600, TimeUnit.MILLISECONDS);

        final OkHttpClient client = b.build();
        //final String name= nameET.getText().toString();
        //final String pass=pwET.getText().toString();
        service.execute(new Runnable() {
            @Override
            public void run() {
                RequestBody formBody = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .url("https://todolist-token.herokuapp.com/list")
                        .header("x-access-token",token)
                        .post(formBody)//
                        .build();
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //showclient.setText("註冊中...");
                        }
                    });

                    Log.d("app", "run: execute");
                    final Response response = client.newCall(request).execute();
                    final String resStr = response.body().string();
                    Log.d("app", "run: resStr: " + resStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("app", "run: execute done");

                            //showclient.setText(resStr);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        service.shutdown();
    }
}

