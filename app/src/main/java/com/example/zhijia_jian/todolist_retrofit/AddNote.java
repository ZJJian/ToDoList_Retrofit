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

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddNote extends AppCompatActivity {
    private EditText editText;
    private EditText texteditText;
    private Button addNoteButton;
    private String token;
    private Long noteId;
    private static final int EDIT=2;
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

    }
    public void onAddButtonClick(View view) {
        if(noteId==-1)
            addNote();
        else
            editNote();


        Intent intent = new Intent();
        //intent.setClass(AddNote.this , ToDoLists.class);
        Bundle bun=new Bundle();
        bun.putString("token",token);
        intent.putExtras(bun);
        //startActivityForResult(intent, EDIT);
        setResult(RESULT_OK,intent);
        finish();
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

        Gson gson = new Gson();
        //將Book物件轉成JSON
        String json = gson.toJson(note);
        new MyTask().execute(json);

    }

    class MyTask extends AsyncTask<String, String, String> {
        protected void onPreExecute(){
            // in main thread
        }

        protected String doInBackground(String... params){
            // in background thread
            //final ExecutorService service = Executors.newFixedThreadPool(10);
            //String getJson;
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.readTimeout(1000*30, TimeUnit.MILLISECONDS);
            b.writeTimeout(600, TimeUnit.MILLISECONDS);

            final OkHttpClient client = b.build();
            RequestBody formBody = RequestBody.create(JSON, params[0]);
            Request request = new Request.Builder()
                    .url("https://todolist-token.herokuapp.com/list/"+noteId.toString())
                    .header("x-access-token",token)
                    .put(formBody)//
                    .build();
            try {


                Log.d("app", "run: execute");
                final Response response = client.newCall(request).execute();
                final String resStr = response.body().string();
                Log.d("app", "run: resStr: " + resStr);
                return resStr;

            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        protected void onProgressUpdate(String... progress){
            // in main thread
        }

        protected void onPostExecute(String result){
            // in main thread
            //showList();

        }

        protected void onCancelled(String result){
            // in main thread

        }

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
        //將Book物件轉成JSON
        String json = gson.toJson(note);
        Log.d("app",json);

        noteToServere(json,token);
        //noteDao.insert(note);
        //Log.d("DaoExample", "Inserted new note, ID: " + note.getId());

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

