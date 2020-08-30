package com.example.whatsapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private String messageRecieverName,messageRecieverId,messageRecieverImage;
    private Toolbar mToolbar;
    private TextView userName,userLastSeen;
    private ImageView userImage;
    private ImageButton sendMessage,sendFilesButton;
    private EditText messageInputText;
    private FirebaseAuth mAuth;
    String messageSenderId;
    private Context context;
    private DatabaseReference rootRef;
    private final List<Messages> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessageList;
    private String saveCurrentTime,saveCurrentDate;
    private String checker = "",myUrl = "";
    private StorageTask uploadTask;
    private Uri fileUri;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = this;
        messageRecieverName = getIntent().getExtras().get("visit_name").toString();
        messageRecieverId = getIntent().getExtras().get("visit_user_id").toString();
        messageRecieverImage = getIntent().getExtras().get("visit_image").toString();
        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        displayLastSeen();

       InitailizeControlers();
       userName.setText(messageRecieverName);
        Picasso.get().load(messageRecieverImage).placeholder(R.drawable.profile_image).into(userImage);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });
        displayLastSeen();
        sendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           CharSequence options[] = new CharSequence[]
                   {
                           "Images",
                           "pdf files",
                           "ms word files"
                   }       ;
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("select the file");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            checker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"select image"),438);

                        }
                        if(i == 1){
                                        checker ="pdf";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent,"select pdf file"),438);

                        }
                        if(i == 2){
                            checker = "docx";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent,"select msword file"),438);


                        }
                    }
                });
                builder.show();
            }
        });

    }


    private void InitailizeControlers() {


        mToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBaraView  = layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBaraView);
        userImage = findViewById(R.id.custom_profile_image);
        userName = findViewById(R.id.custom_profile_name);
        userLastSeen = findViewById(R.id.custom_user_last_seen);
        sendMessage = findViewById(R.id.send_private_message_button);
        messageInputText = findViewById(R.id.inupt_private_message);
        messageAdapter = new MessageAdapter(messageList,context);
        userMessageList = findViewById(R.id.private_messages);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);
        sendFilesButton = findViewById(R.id.send_files_button);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hhh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        loadingBar = new ProgressDialog(ChatActivity.this);



    }
    private void displayLastSeen(){
        rootRef.child("users").child(messageRecieverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("userState").hasChild("state")){
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();
                            if (state.equals("online")){
                                userLastSeen.setText("online");
                            }
                            if (state.equals("offline")){
                                userLastSeen.setText("Last seen:"+"\n"+date+"  "+time);

                            }
                        }
                        else{
                            userLastSeen.setText("offline");


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null){
            loadingBar.setTitle("sending file");
            loadingBar.setMessage("please wait we are sending this file");
            loadingBar.setCanceledOnTouchOutside(false)
            ;
            loadingBar.show();

            fileUri = data.getData();
            if(!checker.equals("image")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("document files");
                final String messageSenderRef ="messages/" + messageSenderId+"/"+ messageRecieverId;
                final String messageRecieverRef ="messages/"  + messageRecieverId+"/"+ messageSenderId;


                DatabaseReference userMessageKeyRef = rootRef.child("messages")
                        .child(messageSenderId).child(messageRecieverId).push();

                final String messagePushId = userMessageKeyRef.getKey();
                final StorageReference filePath = storageReference.child(messagePushId+"."+checker);
                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                               // Uri downloadUrl = task.getResult();
                                //Uri downloadUrl= task.getResult();
                               // myUrl  = downloadUrl.toString();
                                Map messageTextBody = new HashMap();
                                messageTextBody.put("messages",myUrl);
                                messageTextBody.put("name",fileUri.getLastPathSegment());
                                messageTextBody.put("type","image");
                                messageTextBody.put("from",messageSenderId);
                                messageTextBody.put("to",messageRecieverId);
                                messageTextBody.put("messageId",messagePushId);
                                messageTextBody.put("time",saveCurrentTime);
                                messageTextBody.put("date",saveCurrentDate);




                                Map messageBodyDetails =new HashMap();
                                messageBodyDetails.put(messageSenderRef +"/" + messagePushId,messageTextBody);
                                messageBodyDetails.put(messageRecieverRef +"/" + messagePushId,messageTextBody);

                            }
                    }
                });

            }
            else if(checker.equals("image")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image files");
               final String messageSenderRef ="messages/" + messageSenderId+"/"+ messageRecieverId;
               final String messageRecieverRef ="messages/"  + messageRecieverId+"/"+ messageSenderId;


                DatabaseReference userMessageKeyRef = rootRef.child("messages")
                        .child(messageSenderId).child(messageRecieverId).push();

                final String messagePushId = userMessageKeyRef.getKey();
                final StorageReference filePath = storageReference.child(messagePushId+"."+"jpg");
                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri downloadUrl= task.getResult();
                            myUrl  = downloadUrl.toString();
                           // myUrl  = downloadUrl.toString();
                            Map messageTextBody = new HashMap();
                            messageTextBody.put("messages",myUrl);
                            messageTextBody.put("name",fileUri.getLastPathSegment());
                            messageTextBody.put("type","image");
                            messageTextBody.put("from",messageSenderId);
                            messageTextBody.put("to",messageRecieverId);
                            messageTextBody.put("messageId",messagePushId);
                            messageTextBody.put("time",saveCurrentTime);
                            messageTextBody.put("date",saveCurrentDate);




                            Map messageBodyDetails =new HashMap();
                            messageBodyDetails.put(messageSenderRef +"/" + messagePushId,messageTextBody);
                            messageBodyDetails.put(messageRecieverRef +"/" + messagePushId,messageTextBody);
                            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        loadingBar.dismiss();
                                         Toast.makeText(ChatActivity.this, "message sent successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "error", Toast.LENGTH_SHORT).show();
                                    }
                                    messageInputText.setText("");
                                }
                            });
                        }
                    }
                });

            }
            else{
                loadingBar.dismiss();
                Toast.makeText(context, "nothing selected ", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        rootRef.child("users").child(messageSenderId).child("userState").child("state").setValue("online");

        rootRef.child("messages").child(messageSenderId).child(messageRecieverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messageList.add(messages);
               // Messages a = messageList.get(0);

               // t.setText(a.getMessage());
                messageAdapter.notifyDataSetChanged();

                userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendMessage(){
        String messageText = messageInputText.getText().toString();
        if(TextUtils.isEmpty(messageText)){
           // Tost.makeText(this, "please enter message", Toast.LENGTH_SHORT).show();

        }
        else{
            String messageSenderRef ="messages/" + messageSenderId+"/"+ messageRecieverId;
            String messageRecieverRef ="messages/"  + messageRecieverId+"/"+ messageSenderId;


            DatabaseReference userMessageKeyRef = rootRef.child("messages")
                    .child(messageSenderId).child(messageRecieverId).push();

            String messagePushId = userMessageKeyRef.getKey();
            Map messageTextBody = new HashMap();
            messageTextBody.put("messages",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderId);
            messageTextBody.put("to",messageRecieverId);
            messageTextBody.put("messageId",messagePushId);
            messageTextBody.put("time",saveCurrentTime);
            messageTextBody.put("date",saveCurrentDate);




            Map messageBodyDetails =new HashMap();
            messageBodyDetails.put(messageSenderRef +"/" + messagePushId,messageTextBody);
            messageBodyDetails.put(messageRecieverRef +"/" + messagePushId,messageTextBody);
           rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
               if(task.isSuccessful()){
                  // Toast.makeText(ChatActivity.this, "message sent successfully", Toast.LENGTH_SHORT).show();
               }
               else{
                   //Toast.makeText(ChatActivity.this, "error", Toast.LENGTH_SHORT).show();
               }
               messageInputText.setText("");
                }
            });




        }

    }

}










