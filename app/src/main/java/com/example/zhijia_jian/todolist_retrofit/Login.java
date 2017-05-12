package com.example.zhijia_jian.todolist_retrofit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Login extends AppCompatActivity {

    private Button sButton;
    private Button lButton;
    private EditText nameET;
    private EditText pwET;
    private TextView showclient;
    private TextView mainTitle;
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
        intent.setClass(Login.this , ToDoLists.class);
        Bundle bun=new Bundle();
        bun.putString("token",token);
        intent.putExtras(bun);
        startActivity(intent);
        settings = getSharedPreferences(data,0);
        showclient.setText("");
        Toast.makeText(Login.this, "Welcome "+ settings.getString(usernameField,""), Toast.LENGTH_SHORT).show();
    }
    public String readData(){
        settings = getSharedPreferences(data,0);
        return settings.getString(tokenField,"");
//        name.setText(settings.getString(nameField, ""));
//        phone.setText(settings.getString(phoneField, ""));
//        sex.setText(settings.getString(sexField, ""));
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
        final ExecutorService service = Executors.newFixedThreadPool(10);

        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(1000*20, TimeUnit.MILLISECONDS);
        b.writeTimeout(600, TimeUnit.MILLISECONDS);

        final OkHttpClient client = b.build();
        final String name= nameET.getText().toString();
        final String pass=pwET.getText().toString();
        service.execute(new Runnable() {
            @Override
            public void run() {
                RequestBody formBody = new FormBody.Builder()
                        .add("username", name)
                        .add("password", pass)
                        .build();
                Request request = new Request.Builder()
                        .url("https://todolist-token.herokuapp.com/user/register")
                        .post(formBody)
                        .build();
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showclient.setText("Registering...");
                        }
                    });

                    Log.d("app", "run: execute");
                    final Response response = client.newCall(request).execute();
                    final String resStr = response.body().string();
                    Log.d("app", "run: resStr: " + resStr);
                    //showclient.setText(resStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("app", "run: execute done");
                            Toast.makeText(Login.this, resStr, Toast.LENGTH_SHORT).show();
                            //showclient.setText(resStr);
                            if(resStr.trim().equals("OK")) {
                                handleLoginButton();
                            }
                            else
                            {
                                showclient.setText("\""+name+"\" has been registered.");
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        service.shutdown();

    }
    private void handleLoginButton()
    {
        final ExecutorService service = Executors.newFixedThreadPool(10);

        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(1000*20, TimeUnit.MILLISECONDS);
        b.writeTimeout(600, TimeUnit.MILLISECONDS);

        final OkHttpClient client = b.build();
        final String name= nameET.getText().toString();
        final String pass=pwET.getText().toString();
        service.execute(new Runnable() {
            @Override
            public void run() {
                RequestBody formBody = new FormBody.Builder()
                        .add("username", name)
                        .add("password", pass)
                        .build();
                Request request = new Request.Builder()
                        .url("https://todolist-token.herokuapp.com/user/login")
                        .post(formBody)
                        .build();
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showclient.setText("Please wait...");
                        }
                    });
                    final Response response = client.newCall(request).execute();
                    final String resStr = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            final String token=resStr.substring(resStr.indexOf(':')+2,resStr.length()-2);
                            //showclient.setText(resStr+"\n"+token);
                            saveData(token);
                            gotoListPage(token);
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
