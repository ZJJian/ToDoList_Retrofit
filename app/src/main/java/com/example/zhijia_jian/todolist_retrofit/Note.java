package com.example.zhijia_jian.todolist_retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Entity mapped to table "NOTE".
 */
//@Entity(indexes = {
//        @Index(value = "text, date DESC", unique = true)
//})

public class Note {

    //@Id
    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("content")
    @Expose
    private String content;

    private String comment;
    private java.util.Date date;

    @SerializedName("title")
    @Expose
    private String title;



    public Note() {
    }

    public Note(Long id) {
        this.id = id;
    }


    //@Keep
    public Note(Long id, String text, String comment, java.util.Date date, String title) {
        this.id = id;
        this.content = text;
        this.comment = comment;
        this.date = date;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getText() {
        return content;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setText(String text) {
        this.content = text;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}