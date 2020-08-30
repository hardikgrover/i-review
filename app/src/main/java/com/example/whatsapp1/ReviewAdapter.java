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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MessageViewHolder> {
    private List<ReviewModal> UserMessageList;
   // private FirebaseAuth mAuth;
    //private DatabaseReference usersRef;
    private Context context;
    private String currentGroupName,currentDate,currentTime;


    public ReviewAdapter (List<ReviewModal> UserMessageList,Context context,String currentGroupName){
        this.UserMessageList = UserMessageList;
        this.context = context;
        this.currentGroupName = currentGroupName;

    }
    //public  ReviewAdapter(String messageKey){
        //this.messageKey = messageKey;
    //}
   // public  ReviewAdapter(List<CommentsModal> commentsMessageList,String currentDate,String currentTime){
     //   this.commentsMessageList = commentsMessageList;
       // this.currentDate = currentDate;
        //this.currentTime = currentTime;
    //}
    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView dateTime;
        public TextView name;
        public TextView post;
        public TextView line;
        public LinearLayout l2;
        public TextView textImage;
        public ImageView comment;
        public TextView comment1;
       // public TextView comment2;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

          //  comment2 = itemView1.findViewById(R.id.reciever_message_text);
            textImage = itemView.findViewById(R.id.textButton);
            dateTime= itemView.findViewById(R.id.dateTime);
            name = itemView.findViewById(R.id.name);
            post = itemView.findViewById(R.id.post);
            line = itemView.findViewById(R.id.line);
            l2 = itemView.findViewById(R.id.l2);
            comment = itemView.findViewById(R.id.comment_button);
            comment1 = itemView.findViewById(R.id.comment_text1);


        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review,parent,false);
        //View veiw1 = LayoutInflater.from(parent.getContext()).inflate((R.layout.activity_comment),parent,false);
        //mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
       // usersRef = FirebaseDatabase.getInstance().getReference().child("users");



      //  String messageSenderId = mAuth.getCurrentUser().getUid();
        final ReviewModal messages = UserMessageList.get(position);
        String textImage;
        String date = messages.getDate();
        String time = messages.getTime();
        String name = messages.getName();
        String post = messages.getPost();
       // String sub[] = name.split("");
       // int comment = R.drawable.replyicon1;
        //final CommentsModal comments = commentsMessageList.get(position);
        //String comment1 = comments.getComment();
       // holder.comment2.setText(comment1+"\n"+currentDate+""+currentTime);

        //char char1 = name.charAt(0);
        //holder.textImage.setText(char1);
       // holder.setData(time,name,post,line,like,like1,comment,comment1);
        holder.dateTime.setText(date+" "+time);
       holder.post.setText(post);
        holder.name.setText(name);
      //  holder.line.setText(line);
        //holder.comment1.setText(comment);
       // Picasso.get().load(comment).into(holder.comment);


        holder.l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String post = messages.getPost();
                Intent intent = new Intent(context,Comment.class);
                intent.putExtra("post",post);
                intent.putExtra("group name",currentGroupName);
               // intent.putExtra("message key",messageKey);
                context.startActivity(intent);
               // context.startActivity(new Intent(context, Comment.class));


            }


        });




        //String a = UserMessageList.get(0).toString();
        //Toast.makeText(context, a, Toast.LENGTH_SHORT).show();

        //  Toast.makeText(context, fromMessageType, Toast.LENGTH_SHORT).show();



    }

    @Override
    public int getItemCount() {
        return UserMessageList.size();
    }

    }
















