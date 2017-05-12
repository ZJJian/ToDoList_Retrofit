package com.example.zhijia_jian.todolist_retrofit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ToDoLists extends AppCompatActivity {

    private NotesAdapter notesAdapter;
    private List<Note> notes;
    private String token;
    String getJson;
    private Toolbar mToolbar;
    private SharedPreferences settings;
    private static final String data = "DATA";
    private static final String usernameField = "USERNAME";
    private static final String passwordField = "PASSWORD";
    private static final String tokenField = "TOKEN";
    private static final int EDIT=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_lists);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        settings = getSharedPreferences(data,0);

        setUpViews();
        Bundle bun = this.getIntent().getExtras();
        token=bun.getString("token");
        //updateNotes();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent();
                intent.setClass(ToDoLists.this , AddNote.class);
                Bundle bun=new Bundle();
                bun.putString("token",token);
                bun.putLong("noteId",-1);
                intent.putExtras(bun);
                startActivity(intent);
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_edit:
                        handelLogOut();
                        //Toast.makeText( "Edit is clicked!", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        setUpViews();
        updateNotes();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
/*
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
*/

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        //MenuItem refresh = menu.getItem(R.id.menu_edit);
        //refresh.setEnabled(true);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }
    private void handelLogOut() {
        String s=settings.getString(tokenField,"");
        Log.d("App before clear", s);
        settings.edit().remove(usernameField).commit();
        settings.edit().remove(passwordField).commit();
        settings.edit().remove(tokenField).commit();
        s=settings.getString(tokenField,"");
        Log.d("App after clear", s);

        Intent intent = new Intent();
        intent.setClass(ToDoLists.this , Login.class);
        startActivityForResult(intent, EDIT);


    }
    private void updateNotes() {
        //while(getJson==null)
        //getNotesFromServer();
        new MyTask().execute(" ");
//        AsyncTask task = new MyTask();
//        task.execute(" ");
//        task.cancel(true);

    }
    private  void showList()
    {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Note>>(){}.getType();
        notes = gson.fromJson(getJson,listType);
        notesAdapter.setNotes(notes);

    }
    protected void setUpViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewNotes);
        //noinspection ConstantConditions
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notesAdapter = new NotesAdapter(noteClickListener);
        recyclerView.setAdapter(notesAdapter);

    }
    class MyTask extends AsyncTask<String, String, String> {
        protected void onPreExecute(){
            // in main thread
        }

        protected String doInBackground(String... params){
            // in background thread
            //final ExecutorService service = Executors.newFixedThreadPool(10);
            //String getJson;
            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.readTimeout(1000*30, TimeUnit.MILLISECONDS);
            b.writeTimeout(600, TimeUnit.MILLISECONDS);

            final OkHttpClient client = b.build();
            Request request = new Request.Builder()
                    .url("https://todolist-token.herokuapp.com/list")
                    .header("x-access-token",token)
                    .get()//
                    .build();
            try {


                Log.d("app", "run: execute");
                final Response response = client.newCall(request).execute();
                final String resStr = response.body().string();
                Log.d("app", "run: resStr: " + resStr);
                getJson=resStr;
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
            showList();
        }

        protected void onCancelled(String result){
            // in main thread

        }

    }
    NotesAdapter.NoteClickListener noteClickListener = new NotesAdapter.NoteClickListener() {
        @Override
        public void onNoteClick(int position) {
            Note note = notesAdapter.getNote(position);

            getAlertDialog(note,"Edit or Delete this message").show();


        }
    };
    private AlertDialog getAlertDialog(final Note note, String message){


        final Long noteId = note.getId();

//        //產生一個Builder物件
        AlertDialog.Builder builder = new AlertDialog.Builder(ToDoLists.this);
//        //設定Dialog的標題
        builder.setTitle(note.getTitle());
//        //設定Dialog的內容
        builder.setMessage(note.getText());
        //設定Positive按鈕資料
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //按下按鈕時顯示快顯

                //noteDao.deleteByKey(noteId);
                notes.remove(getIndex(noteId,notes));
                notesAdapter.setNotes(notes);
                new DeleteTask().execute(noteId.toString());
                Log.d("DaoExample", "Deleted note, ID: " + noteId);


                //updateNotes();
                Toast.makeText(ToDoLists.this, "You clicked \"delete\"", Toast.LENGTH_SHORT).show();
            }
        });
        //設定Negative按鈕資料
        builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent();
                intent.setClass(ToDoLists.this , AddNote.class);
                Bundle bun=new Bundle();
                bun.putLong("noteId",noteId);
                bun.putString("token",token);
                bun.putString("content",note.getText());
                bun.putString("title",note.getTitle());
                intent.putExtras(bun);
                startActivity(intent);
                //按下按鈕時顯示快顯
                Toast.makeText(ToDoLists.this, "You clicked \"Edit\"", Toast.LENGTH_SHORT).show();
            }
        });
        //利用Builder物件建立AlertDialog
        return builder.create();
    }
    public int getIndex(Long noteId,List<Note> notes)
    {
        for (int i = 0; i < notes.size(); i++)
        {
            Note n=notes.get(i);
            if (n.getId()==noteId)
            {
                return i;
            }
        }

        return -1;
    }
    class DeleteTask extends AsyncTask<String, String, String> {
        protected void onPreExecute(){
            // in main thread
        }

        protected String doInBackground(String... params){
            // in background thread

            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.readTimeout(1000*30, TimeUnit.MILLISECONDS);
            b.writeTimeout(600, TimeUnit.MILLISECONDS);

            final OkHttpClient client = b.build();
            Request request = new Request.Builder()
                    .url("https://todolist-token.herokuapp.com/list/"+params[0])
                    .header("x-access-token",token)
                    .delete()//
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
            //new MyTask().execute(" ");
        }

        protected void onCancelled(String result){
            // in main thread

        }

    }

}

