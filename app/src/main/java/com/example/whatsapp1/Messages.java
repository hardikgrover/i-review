package com.example.whatsapp1;

import android.content.Context;
import android.widget.Toast;

public class Messages {
    private String from;
    private String messages;
    private String type;
    private String to;
    private String time;

    public Messages(String from, String messages, String type, String to, String time, String date, String messageId, String name, Context context) {
        this.from = from;
        this.messages = messages;
        this.type = type;
        this.to = to;
        this.time = time;
        this.date = date;
        this.messageId = messageId;
        this.name = name;
        this.context = context;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private String date;
    private String messageId;
    private String name;
    private Context context;



   public Messages(){

   }
}
