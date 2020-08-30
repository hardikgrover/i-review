package com.example.whatsapp1;

public class CommentsModal {

    public CommentsModal(){

    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }



    public CommentsModal(String comment) {
        this.comment = comment;

    }

    String comment;
}
