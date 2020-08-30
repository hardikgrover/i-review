package com.example.whatsapp1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MessageViewHolder> {
    private List<ReviewModal> UserMessageList;
    private List<CommentsModal> commentsMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private Context context;
    private String currentGroupName,messageKey,currentDate,currentTime;




    public  CommentsAdapter(List<CommentsModal> commentsMessageList,String currentDate,String currentTime){
        this.commentsMessageList = commentsMessageList;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView comment;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.reciever_message_text);



        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_chat_layout_file,parent,false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
        // usersRef = FirebaseDatabase.getInstance().getReference().child("users");



        //  String messageSenderId = mAuth.getCurrentUser().getUid();

        final CommentsModal comments = commentsMessageList.get(position);
        String comment1 = comments.getComment();
        //Toast.makeText(context, comment1, Toast.LENGTH_SHORT).show();
        holder.comment.setText(comment1);



        // holder.setData(time,name,post,line,like,like1,comment,comment1);




        //String a = UserMessageList.get(0).toString();
        //Toast.makeText(context, a, Toast.LENGTH_SHORT).show();

        //  Toast.makeText(context, fromMessageType, Toast.LENGTH_SHORT).show();



    }

    @Override
    public int getItemCount() {
        return commentsMessageList.size();
    }

}
















