package com.example.zhijia_jian.todolist_retrofit;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.util.Date;

import retrofit2.Call;

public class AddNoteActivity extends AppCompatActivity {
    private EditText editText;
    private EditText texteditText;
    private Button addNoteButton;
    //private String token;
    private Long noteId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        addNoteButton = (Button)findViewById(R.id.button_add);
        editText = (EditText) findViewById(R.id.titleET);
        texteditText = (EditText) findViewById(R.id.textET);

        Bundle bun = this.getIntent().getExtras();
//        token=bun.getString("token");
//        Log.d("app", token);
        noteId=bun.getLong("noteId");
        if(noteId != -1) {
            editText.setText(bun.getString("title"));
            texteditText.setText(bun.getString("content"));
            addNoteButton.setText("Update");
        }

    }

    public void onAddButtonClick(View view) {
        String noteText = editText.getText().toString();
        String textnoteText = texteditText.getText().toString();

        Note note = new Note();
        note.setTitle(noteText);
        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment =(noteId == -1)?"Added on " + df.format(new Date()) : "Edited on " + df.format(new Date());
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
        NoteClient myClient = NoteClient.getInstance();
        Call<String> call = myClient.update(noteId, note);

        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.d("APP", "UpdateNoteonResponse: "+response.body().toString());
                Intent intent = new Intent();
//                Bundle bun=new Bundle();
//                bun.putString("token",token);
//                intent.putExtras(bun);
                setResult(RESULT_OK,intent);
                finish();


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("APP", "UpdateNoteonFailure: ");
            }
        });
    }

    private void AddOneNote(Note note) {

        NoteClient myClient=NoteClient.getInstance();
        Call<String> call = myClient.post(note);

        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.d("APP", "AddOneNoteResponse: "+response.body().toString());

                Intent intent = new Intent();
//                Bundle bun=new Bundle();
//                bun.putString("token",token);
//                intent.putExtras(bun);
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

