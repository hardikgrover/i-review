package com.example.whatsapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private String recieverUserId,senderUserId;
    private CircleImageView userProfileImage;
    private Button sendMessage,declineRequest;
    private TextView status,name;
    private DatabaseReference mRef,chatRef,contactsRef,notificationReferance;
    private FirebaseAuth mAuth;
    private String currentState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        currentState = "new";
        chatRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        mRef = FirebaseDatabase.getInstance().getReference().child("users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationReferance = FirebaseDatabase.getInstance().getReference().child("Notifications");

        recieverUserId = getIntent().getExtras().get("visit_user_id").toString();
        userProfileImage = findViewById(R.id.visit_profile_image);
        name = findViewById(R.id.visit_user_name);
        status = findViewById(R.id.visit_user_status);
        sendMessage = findViewById(R.id.send_message_request_button);
        declineRequest = findViewById(R.id.decline_request_button);

        RetrieveUserInfo();
    }


    private void RetrieveUserInfo() {
        mRef.child(recieverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && (dataSnapshot.hasChild("image"))) {
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    name.setText(userName);
                    status.setText(userStatus);
                    ManageChatRequest();


                } else {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    name.setText(userName);
                    status.setText(userStatus);
                    ManageChatRequest();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ManageChatRequest() {
        chatRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(recieverUserId)){
                            String request_type = dataSnapshot.child(recieverUserId).child("request_type").getValue().toString();
                            if(request_type.equals("sent")){
                                currentState = "request_sent";
                                sendMessage.setText("cancel chat request");
                            }
                            else if(request_type.equals("recieved")){
                                currentState = "request_recieved";
                                sendMessage.setText("Accept Chat Request");

                                declineRequest.setVisibility(View.VISIBLE);
                                declineRequest.setEnabled(true);
                                declineRequest.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CancelChatRequest();
                                    }
                                });

                            }

                        }
                        else{
                            contactsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(recieverUserId)){
                                                currentState="friends";
                                                sendMessage.setText("remove this contact");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        if(!senderUserId.equals(recieverUserId)){
         sendMessage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 sendMessage.setEnabled(false);

                 if(currentState.equals("new")){
                     SendChatRequest();
                 }
                 if(currentState.equals("request_sent")){
                     CancelChatRequest();

                 }
                 if(currentState.equals("request_recieved")){

                     AcceptChatRequest();

                 }
                 if(currentState.equals("friends")){

                     RemoveContact();

                 }

             }
         });
        }
        else{
            sendMessage.setVisibility(View.INVISIBLE);
        }


    }

    private void RemoveContact() {
        contactsRef.child(senderUserId).child(recieverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactsRef.child(recieverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendMessage.setEnabled(true);
                                                currentState = "new";
                                                sendMessage.setText("Send Message");
                                                declineRequest.setVisibility(View.INVISIBLE);
                                                declineRequest.setEnabled(false);
                                            }
                                        }
                                    });

                        }
                    }
                });

    }

    private void AcceptChatRequest() {
        contactsRef.child(senderUserId).child(recieverUserId)
                .child("Contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactsRef.child(recieverUserId).child(senderUserId)
                                    .child("Contacts").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                chatRef.child(senderUserId).child(recieverUserId)
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            chatRef.child(recieverUserId).child(senderUserId)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                            sendMessage.setEnabled(true);
                                                                                            currentState = "friends";
                                                                                            sendMessage.setText("remove this contact");
                                                                                            declineRequest.setVisibility(View.INVISIBLE);
                                                                                            declineRequest.setEnabled(false);
                                                                            }
                                                                        }
                                                                    })    ;
                                                        }
                                                    }
                                                })    ;
                                            }
                                        }
                                    });

                        }
                    }
                });

    }

    private void CancelChatRequest() {
        chatRef.child(senderUserId).child(recieverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        chatRef.child(recieverUserId).child(senderUserId)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            sendMessage.setEnabled(true);
                                            currentState = "new";
                                            sendMessage.setText("Send Message");
                                            declineRequest.setVisibility(View.INVISIBLE);
                                            declineRequest.setEnabled(false);
                                        }
                                    }
                                });

                    }
                    }
                });
    }

    private void SendChatRequest() {
        chatRef.child(senderUserId).child(recieverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful()){
                       chatRef.child(recieverUserId).child(senderUserId)
                               .child("request_type").setValue("recieved")
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()){
                                           HashMap<String,String> chatNotificationMap = new HashMap<>();
                                           chatNotificationMap.put("from",senderUserId);
                                           chatNotificationMap.put("type","request");
                                           notificationReferance.child(recieverUserId).push()
                                                   .setValue(chatNotificationMap).addOnCompleteListener(
                                                   new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                sendMessage.setEnabled(true);
                                                                currentState = "request_sent";
                                                                sendMessage.setText("cancel chat request");

                                                            }
                                                       }
                                                   }
                                           );



                                    }
                                   }
                               });
                   }
                    }
                });

    }
}
