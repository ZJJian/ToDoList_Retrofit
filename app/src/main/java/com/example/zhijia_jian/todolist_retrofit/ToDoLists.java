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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Callback;
//import okhttp3.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.Call;
//import retrofit2.Response;
//import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ToDoLists extends AppCompatActivity {
    public static final String TAG = "app";
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

    private NoteApi mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_lists);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        settings = getSharedPreferences(data,0);
        mToolbar.setTitle(settings.getString(usernameField,"")+"'s ToDoList");
        setUpViews();
        Bundle bun = this.getIntent().getExtras();
        token=bun.getString("token");

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

                        break;
                }
                return false;
            }
        });


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

    @Override
    public void onResume(){
        super.onResume();

        setUpViews();
        updateNotes();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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

    protected void setUpViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewNotes);
        //noinspection ConstantConditions
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notesAdapter = new NotesAdapter(noteClickListener);
        recyclerView.setAdapter(notesAdapter);

    }
    private void updateNotes()
    {
        Call<List<Note>> call = mService.getNoteList();

        call.enqueue(new retrofit2.Callback<List<Note>>() {
            @Override
            public void onResponse(Call<List<Note>> call, retrofit2.Response<List<Note>> response) {
                Log.d(TAG, "onResponse: ");
                notes = response.body();
                notesAdapter.setNotes(notes);
            }

            @Override
            public void onFailure(Call<List<Note>> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }
    private void DeleteNote(Long noteId)
    {
        Log.d(TAG, "DeleteNote: ");
        Call<String> call = mService.delete(noteId);

        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.d(TAG, "DeleteNoteonResponse: "+response.body().toString());

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "DeleteNoteonFailure: ");
            }
        });
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


        AlertDialog.Builder builder = new AlertDialog.Builder(ToDoLists.this);
        builder.setTitle(note.getTitle());
        builder.setMessage(note.getText());
        //set "delete" button
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                notes.remove(getIndex(noteId,notes));
                notesAdapter.setNotes(notes);
                DeleteNote(noteId);

                Log.d("APP", "Deleted note, ID: " + noteId);

                Toast.makeText(ToDoLists.this, "You clicked \"delete\"", Toast.LENGTH_SHORT).show();
            }
        });
        //set "edit" button
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

                Toast.makeText(ToDoLists.this, "You clicked \"Edit\"", Toast.LENGTH_SHORT).show();
            }
        });

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

}

