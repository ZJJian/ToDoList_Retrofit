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
    String CONTENT = "content";

    //文章列表
    @GET("/list")
    Call<List<Note>> getNoteList();

    //取得某篇文章
    @GET("/list/{id}")
    Call<Note> getNote(
            @Path("id") int id
    );

    //新增文章
    @POST("/list")
    @FormUrlEncoded
    Note create(
            @Field(CONTENT) String content
    );

    //編輯文章
    @PUT("/list/{id}")
    @FormUrlEncoded
    Note update(
            @Path("id") int id,
            @Field(CONTENT) String content
    );

    //編輯文章
    @PUT("/list/{id}")
    Note update(
            @Path("id") int id,
            @Body Note note
    );

    //刪除文章
    @DELETE("/list/{id}")
    Response delete(@Path("id") int id);
}