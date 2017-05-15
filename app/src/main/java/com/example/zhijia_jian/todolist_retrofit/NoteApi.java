package com.example.zhijia_jian.todolist_retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NoteApi {

    //Login
    @POST("/user/login")
    @FormUrlEncoded
    Call<Token> login(@Field("username") String username,@Field("password") String password);


    //Login
    @POST("/user/register")
    @FormUrlEncoded
    Call<String> register(@Field("username") String username,@Field("password") String password);


    //get note list
    @GET("/list")
    Call<List<Note>> getNoteList();

    //get a note
    @GET("/list/{id}")
    Call<Note> getNote(
            @Path("id") int id
    );

    //add a new note
    //@FormUrlEncoded
    @POST("/list")
    Call<String> post(@Body Note note);



    //edit a note
    @PUT("/list/{id}")
    Call<String> update(
            @Path("id") Long id,
            @Body Note note
    );

    //delete a note
    @DELETE("/list/{id}")
    Call<String> delete(@Path("id") Long id);
}