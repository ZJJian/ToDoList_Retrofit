package com.example.zhijia_jian.todolist_retrofit;

import retrofit2.Retrofit;

public class ApiClient {

    //private final RestAdapter restAdapter;
    private final NoteApi noteApi;


    public ApiClient() {
        //這邊的使用情境為全部 API 都對同一個 Server 操作
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .build();

        noteApi = retrofit.create(NoteApi.class);

    }

    /*
     * 簡單示範幾個方法，實際使用可在這裡作內容驗證
     */

//    public Note showNote(int id) {
//        return noteApi.getNote(id);
//    }

//    public Comment createComment(int articleId, String content) {
//        return commentsApi.create(articleId, content);
//    }
//
//    //只能透過傳入 Comment 物件刪除，避免誤傳如文章 id 之類的
//    public boolean deleteComment(Comment comment) {
//        return deleteComment(comment.id);
//    }
//
//    //將根據 id 刪除評論的方法封裝起來
//    private boolean deleteComment(int id) {
//        Response response = commentsApi.delete(id);
//        final int statusCode = response.getStatus();
//
//        return 200 <= statusCode && statusCode < 300;
//    }
}