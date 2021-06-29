package com.hk210.postit;

import com.google.firebase.Timestamp;

import java.util.Date;

public class PostModel {

    String description;
    String post;
    String title;
    String userid;
    Date timestamp;










    public PostModel() {
    }

    public PostModel(String description, String post, String title, String userid,Date timestamp) {
        this.description = description;
        this.post = post;
        this.title = title;
        this.userid = userid;
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }



}
