package com.example.zhijia_jian.todolist_retrofit;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {

    private Button sButton;
    private Button lButton;
    private EditText nameET;
    private EditText pwET;
    private TextView showclient;
    private TextView mainTitle;
    private TextView forgotTV;
    private SharedPreferences settings;
    private static final String data = "DATA";
    private static final String usernameField = "USERNAME";
    private static final String passwordField = "PASSWORD";
    private static final String tokenField = "TOKEN";





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
                Toast.makeText(LoginActivity.this, "Forgot Password", Toast.LENGTH_SHORT).show();
                //forgotTV.setTextColor(Color.BLACK);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                startActivity(intent);

            }
        });


        mainTitle =(TextView) findViewById(R.id.tv2);
        mainTitle.setTypeface(Typeface.createFromAsset(getAssets()
                , "fonts/dolphin.ttf"));

        showclient =(TextView)findViewById(R.id.tv3);

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


        String token=readData();
        if(!token.equals(""))
        {
            gotoListPage(token);
        }



    }
    public void gotoListPage(String token)
    {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this , ToDoListsActivity.class);
        Bundle bun=new Bundle();
        bun.putString("token",token);
        intent.putExtras(bun);
        startActivity(intent);
        settings = getSharedPreferences(data,0);
        showclient.setText("");
        Toast.makeText(LoginActivity.this, "Welcome "+ settings.getString(usernameField,""), Toast.LENGTH_SHORT).show();
    }
    public String readData(){
        settings = getSharedPreferences(data,0);
        return settings.getString(tokenField,"");

    }
    public void saveData(String token){
        settings = getSharedPreferences(data,0);
        settings.edit()
                .putString(usernameField, nameET.getText().toString())
                .putString(passwordField, pwET.getText().toString())
                .putString(tokenField,token)
                .commit();
    }
    private void handleSignupButton()
    {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showclient.setText("Registering...");
            }
        });
        final String name= nameET.getText().toString();
        final String pass=pwET.getText().toString();
        NoteClient myClient=NoteClient.getUserService();
        retrofit2.Call<String> call = myClient.Register(name,pass);

        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(retrofit2.Call<String> call, retrofit2.Response<String> response) {
                Log.d("APP", "SignupButton onResponse: "+response.code());
                if(response.code()==406 && response.message().toString().equals("Not Acceptable")) {
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
    private void handleLoginButton()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showclient.setText("Please wait...");
            }
        });
        final String name= nameET.getText().toString();
        final String pass=pwET.getText().toString();
        NoteClient myClient=NoteClient.getUserService();
        retrofit2.Call<Token> call = myClient.Login(name,pass);
//        try {
//             retrofit2.Response<Token> response = call.execute();
//             Token t = response.body();
//        } catch (Exception ex) {
//
//        }

        call.enqueue(new retrofit2.Callback<Token>() {
            @Override
            public void onResponse(retrofit2.Call<Token> call, retrofit2.Response<Token> response) {
                Log.d("APP", "LoginButton onResponse: "+response.code());

                if (response.code()==406 && response.message().toString().equals("Not Acceptable"))
                    showclient.setText("Username or Password is not correct!");
                else {

                    Token token =response.body();
                    saveData(token.getToken());
                    gotoListPage(token.getToken());

                }
            }

            @Override
            public void onFailure(retrofit2.Call<Token> call, Throwable t) {
                Log.d("APP", "LoginButton onFailure: " + t.toString());
            }
        });
    }



}
