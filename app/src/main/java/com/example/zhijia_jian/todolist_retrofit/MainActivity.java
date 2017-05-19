package com.example.zhijia_jian.todolist_retrofit;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhijia_jian.todolist_retrofit.Activity.ToDoListsActivity;
import com.example.zhijia_jian.todolist_retrofit.Services.NoteClient;


public class MainActivity extends AppCompatActivity {

    private Button sButton;
    private Button lButton;
    private EditText nameET;
    private EditText pwET;
    private TextView showclient;
    private TextView mainTitle;
    private TextView forgotTV;

    NoteClient myClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameET=(EditText) findViewById(R.id.nameET);
        pwET=(EditText) findViewById(R.id.passwordET);
        lButton=(Button)findViewById(R.id.loginButton);
        sButton=(Button)findViewById(R.id.signupButton);
        forgotTV=(TextView) findViewById(R.id.forgotPassword);

        forgotTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Forgot Password", Toast.LENGTH_SHORT).show();
                //forgotTV.setTextColor(Color.BLACK);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                startActivity(intent);

            }
        });


        mainTitle = (TextView) findViewById(R.id.tv2);
        mainTitle.setTypeface(Typeface.createFromAsset(getAssets()
                , "fonts/dolphin.ttf"));

        showclient = (TextView)findViewById(R.id.tv3);

        lButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                handleLoginButton();
            }
        });

        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignupButton();
            }
        });


        myClient=NoteClient.getInstance();
        myClient.setContext(this);

        if(myClient.alreadyLogin()) {
            gotoListPage();
        }

    }

    public void gotoListPage() {

        Intent intent = new Intent();
        intent.setClass(MainActivity.this , ToDoListsActivity.class);
        startActivity(intent);
        Toast.makeText(MainActivity.this, "Welcome "+ myClient.getUsername(), Toast.LENGTH_SHORT).show();

    }

    private void handleSignupButton() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showclient.setText("Registering...");
            }
        });

        final String name= nameET.getText().toString();
        final String pass=pwET.getText().toString();
        myClient=NoteClient.getInstance();
        retrofit2.Call<String> call = myClient.register(name,pass);

        call.enqueue(new retrofit2.Callback<String>() {

            @Override
            public void onResponse(retrofit2.Call<String> call, retrofit2.Response<String> response) {

                Log.d("APP", "SignupButton onResponse: "+response.code());
                if(response.code()==406 && response.message().equals("Not Acceptable")) {
                    showclient.setText("\""+name+"\" has been registered.");
                }
                else {
                    handleLoginButton();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<String> call, Throwable t) {
                Log.d("APP", "SignupButton onFailure: ");
            }
        });
    }

    private void handleLoginButton() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showclient.setText("Please wait...");
            }
        });
        final String name= nameET.getText().toString();
        final String pass=pwET.getText().toString();

        myClient=NoteClient.getInstance();
        Boolean loginSuccess = myClient.login(name,pass);

        if(loginSuccess) {
            gotoListPage();

        } else {
            showclient.setText("Username or Password is not correct!");

        }

    }

}
