package com.example.whatsapp1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> UserMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private Context context;


    public MessageAdapter (List<Messages> UserMessageList,Context context){
        this.UserMessageList = UserMessageList;
        this.context = context;

    }
    public class MessageViewHolder extends RecyclerView.ViewHolder{
       public TextView senderMessageText,recieverMessageText;
         public CircleImageView circleImageView;
         public ImageView messageSenderPicture,messageRecieverPicture;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            recieverMessageText = itemView.findViewById(R.id.reciever_message_text);
            circleImageView = itemView.findViewById(R.id.message_profile_image);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            messageRecieverPicture = itemView.findViewById(R.id.message_reciever_image_view);
        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_messages_layout,parent,false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {

        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = UserMessageList.get(position);
        String fromUserId = messages.getFrom();
        String fromMessageType = messages.getType();
        //String a = UserMessageList.get(0).toString();
        //Toast.makeText(context, a, Toast.LENGTH_SHORT).show();

      //  Toast.makeText(context, fromMessageType, Toast.LENGTH_SHORT).show();


        usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(fromUserId);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("image")){
                    String recieverImage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(recieverImage).placeholder(R.drawable.profile_image).into(holder.circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.recieverMessageText.setVisibility(View.GONE);
        holder.circleImageView.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageRecieverPicture.setVisibility(View.GONE);
            if(fromMessageType.equals("text")){


                if(fromUserId.equals(messageSenderId)){
                    holder.senderMessageText.setVisibility(View.VISIBLE);

                    holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_resource_file);
                     holder.senderMessageText.setText(messages.getMessages()+"\n\n"+messages.getTime()+"-"+messages.getDate());
                     //String a = messages.getMessage();
                    //Toast.makeText(context, a, Toast.LENGTH_SHORT).show();
                }
                else{
                    holder.circleImageView.setVisibility(View.VISIBLE);
                    holder.recieverMessageText.setVisibility(View.VISIBLE);

                    holder.recieverMessageText.setBackgroundResource(R.drawable.reciever_messages_layout);

                    holder.recieverMessageText.setText(messages.getMessages()+"\n\n"+messages.getTime()+"-"+messages.getDate());



                }
            }
        else if(fromMessageType.equals("image")) {
            if(fromUserId.equals(messageSenderId)){
                holder.messageSenderPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessages()).into(holder.messageSenderPicture);

            }
            else{
                holder.messageRecieverPicture.setVisibility(View.VISIBLE);
                holder.circleImageView.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessages()).into(holder.messageRecieverPicture);

            }

            }


        if(fromUserId.equals(messageSenderId)){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(UserMessageList.get(position).getType().equals("image"))
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete for me",
                                        "view this image",
                                        "cancel",
                                        "delete for everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    deleteSentMessages(position,holder);

                                } else if (i == 1) {
                                    Intent intent = new Intent(holder.itemView.getContext(),ImageViewer.class);
                                    intent.putExtra("url",UserMessageList.get(position).getMessages());
                                    holder.itemView.getContext().startActivity(intent);


                                }  else if (i == 3) {
                                    deleteEveryOnedMessages(position, holder);

                                }
                            }
                        });
                        builder.show();
                    }
                    else if(UserMessageList.get(position).getType().equals("text"))
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete for me",

                                        "cancel",
                                        "delete for everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    deleteSentMessages(position,holder);

                                } else if (i == 2) {
                                    deleteEveryOnedMessages(position, holder);

                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }
        else{
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(UserMessageList.get(position).getType().equals("image"))
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete ",
                                        "view this image",
                                        "cancel",

                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    deleterecievedMessages(position,holder);


                                } else if (i == 1) {
                                    Intent intent = new Intent(holder.itemView.getContext(),ImageViewer.class);
                                    intent.putExtra("url",UserMessageList.get(position).getMessages());
                                    holder.itemView.getContext().startActivity(intent);


                                }
                            }
                        });
                        builder.show();
                    }
                    else if(UserMessageList.get(position).getType().equals("text"))
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete",

                                        "cancel",

                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    deleterecievedMessages(position,holder);


                                } else if (i == 1) {

                                }
                            }
                        });
                        builder.show();
                    }
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return UserMessageList.size();
    }
    private void deleteSentMessages(final int position,final MessageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("messages")
                .child(UserMessageList.get(position).getFrom())
                .child(UserMessageList.get(position).getTo())
                .child(UserMessageList.get(position).getMessageId())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "message deleted successfully" , Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void deleterecievedMessages(final int position,final MessageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("messages")
                .child(UserMessageList.get(position).getTo())
                .child(UserMessageList.get(position).getFrom())
                .child(UserMessageList.get(position).getMessageId())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "message deleted successfully" , Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void deleteEveryOnedMessages(final int position,final MessageViewHolder holder){
       final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("messages")
                .child(UserMessageList.get(position).getTo())
                .child(UserMessageList.get(position).getFrom())
                .child(UserMessageList.get(position).getMessageId())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    rootRef.child("messages")
                            .child(UserMessageList.get(position).getFrom())
                            .child(UserMessageList.get(position).getTo())
                            .child(UserMessageList.get(position).getMessageId())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(context, "messages deleted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Toast.makeText(context, "message deleted successfully" , Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}













