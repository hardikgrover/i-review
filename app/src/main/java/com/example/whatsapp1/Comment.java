package com.example.whatsapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Comment extends AppCompatActivity {
    private TextView t1;
    private ImageButton commentImageButton;
    private EditText postText;
    private DatabaseReference groupCommentRef,groupMessageKeyReference;
    private String currentTime,currentDate,messageKey,groupName;
    private RecyclerView commentList;
    private CommentsAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<CommentsModal> userCommentslist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        t1 = findViewById(R.id.t1);
       // Toast.makeText(this, messageKey, Toast.LENGTH_SHORT).show();
        Intent intent = getIntent();
        String message = intent.getStringExtra("post");
        String groupId = intent.getStringExtra("groupKey");
        String messageKey = intent.getStringExtra("messageKey");
        String groupName = intent.getStringExtra("groupName");
         //groupName = intent.getStringExtra("group name");
        // messageKey = intent.getStringExtra("message key");
        //Toast.makeText(this, groupName, Toast.LENGTH_SHORT).show();
        t1.setText(message);
        InitializeFields();
        commentImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });
        //GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupName);
        groupCommentRef = FirebaseDatabase.getInstance().getReference().child("group message ke message").child(groupId)
        .child(groupName).child(messageKey);
        //.child(groupId)
        //.child(groupName).child(messageKey);

    }

    private void uploadData() {

        String text = postText.getText().toString();
        if(TextUtils.isEmpty(text)){
            Toast.makeText(this, "enter the message", Toast.LENGTH_SHORT).show();
        }
        else
        {
            messageKey = groupCommentRef.push().getKey();

            //String groupKey = groupRef.push().getKey();
            //String commentKey =
            Calendar callForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM  dd  yyyy" );
            currentDate = currentDateFormat.format(callForDate.getTime());
            Calendar callForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh mm a" );
            currentTime = currentTimeFormat.format(callForTime.getTime());

            HashMap<String,Object> map = new HashMap<>();
            map.put("comment",text);
            groupMessageKeyReference =groupCommentRef.child(messageKey);

            groupMessageKeyReference.updateChildren(map);


        }
        postText.setText("");

    }

    private void InitializeFields() {
        commentImageButton = findViewById(R.id.post_image_button);
        postText = findViewById(R.id.post_text);
        adapter = new CommentsAdapter(userCommentslist,currentDate,currentTime);
        commentList = findViewById(R.id.comment_recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        commentList.setLayoutManager(linearLayoutManager);
        commentList.setAdapter(adapter);


    }
    @Override
    protected void onStart() {
        super.onStart();
        groupCommentRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                   CommentsModal messages = dataSnapshot.getValue(CommentsModal.class);
                    userCommentslist.add(messages);

                    adapter.notifyDataSetChanged();

                    commentList.smoothScrollToPosition(commentList.getAdapter().getItemCount());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
//                    ReviewModal messages = dataSnapshot.getValue(ReviewModal.class);
//                    messageList.add(messages);
//                    // Messages a = messageList.get(0);
//
//                    // t.setText(a.getMessage());
//                    messageAdapter.notifyDataSetChanged();
//
//                    userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
                        }
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

}
