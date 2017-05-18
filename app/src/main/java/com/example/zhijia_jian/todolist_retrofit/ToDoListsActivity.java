package com.example.zhijia_jian.todolist_retrofit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.List;
import retrofit2.Call;


public class ToDoListsActivity extends AppCompatActivity {
    public static final String TAG = "app";
    private NotesAdapter notesAdapter;
    private List<Note> notes;
    //private String token;
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


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setUpViews();
//        Bundle bun = this.getIntent().getExtras();
//        token=bun.getString("token");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent();
                intent.setClass(ToDoListsActivity.this , AddNoteActivity.class);
                Bundle bun=new Bundle();
                //bun.putString("token",token);
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
        return true;

    }
    private void handelLogOut() {

        NoteClient myClient=NoteClient.getInstance();
        myClient.logout();
        Intent intent = new Intent();
        intent.setClass(ToDoListsActivity.this , LoginActivity.class);
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
        NoteClient myClient=NoteClient.getInstance();
        Call<List<Note>> call = myClient.getNoteList();

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
        NoteClient myClient=NoteClient.getInstance();
        Call<String> call = myClient.delete(noteId);

        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.d(TAG, "DeleteNoteonResponse: "+response.body());

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


        AlertDialog.Builder builder = new AlertDialog.Builder(ToDoListsActivity.this);
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

                Toast.makeText(ToDoListsActivity.this, "You clicked \"delete\"", Toast.LENGTH_SHORT).show();
            }
        });
        //set "edit" button
        builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent();
                intent.setClass(ToDoListsActivity.this , AddNoteActivity.class);
                Bundle bun=new Bundle();
                bun.putLong("noteId",noteId);
                //bun.putString("token",token);
                bun.putString("content",note.getText());
                bun.putString("title",note.getTitle());
                intent.putExtras(bun);
                startActivity(intent);

                Toast.makeText(ToDoListsActivity.this, "You clicked \"Edit\"", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
    public int getIndex(long noteId,List<Note> notes)
    {
        for (int i = 0; i < notes.size(); i++)
        {
            Note n=notes.get(i);
            if (n.getId() == noteId)
            {
                return i;
            }
        }
        return -1;
    }

}

