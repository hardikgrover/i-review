package com.example.whatsapp1;

import android.icu.text.Normalizer2;
import android.widget.Toast;

public class ReviewModal {

    private String time;
    private String post;
    private String city;
    private String date;
    private String name;
public ReviewModal(){

}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public ReviewModal(String time, String date, String name, String post, String city) {
        this.time = time;
        this.date = date;
        this.name = name;
        this.post = post;
        this.city = city;
    }



    }
