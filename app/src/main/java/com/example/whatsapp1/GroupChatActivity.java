package com.example.whatsapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private ScrollView mScrollView;
    private TextView displayTextMessage;
    private final List<ReviewModal> messageList = new ArrayList<>();
    //private Context context = this;
    private EditText userMessageInput;
    private String currentGroupName,currentGroupId,currentUserName ,currentDate,currentTime,messageKey,currentUserId,userCity;
    private FirebaseAuth mauth;
    private DatabaseReference UserRef,GroupNameRef,groupMessageKeyReference;
    private ReviewAdapter messageAdapter;
    private RecyclerView userMessageList;


    private LinearLayoutManager linearLayoutManager;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_chat);
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        currentGroupId = getIntent().getExtras().get("groupKey").toString();
        //Toast.makeText(this,messageKey , Toast.LENGTH_SHORT).show();

        mauth = FirebaseAuth.getInstance();
       currentUserId = mauth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Group messages").child(currentGroupId).child(currentGroupName);


        InitializeFields();
        GetUserInfo();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveMessageInfoToDatabase();
                //userMessageInput.setText("");
                //mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });




}

   private void SaveMessageInfoToDatabase() {
         messageKey = GroupNameRef.push().getKey();
         //messageAdapter = new ReviewAdapter(messageKey);
       //Toast.makeText(this,messageKey , Toast.LENGTH_SHORT).show();

       String post = userMessageInput.getText().toString();
        if(TextUtils.isEmpty(post)){
            Toast.makeText(this, "please write message", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar callForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM" );
            currentDate = currentDateFormat.format(callForDate.getTime());
            Calendar callForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a" );
            currentTime = currentTimeFormat.format(callForTime.getTime());

           // HashMap<String,Object> groupMessageKey = new HashMap<>();
            //GroupNameRef.updateChildren(groupMessageKey);

            groupMessageKeyReference = GroupNameRef.child(messageKey);

            HashMap<String,Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("post",post);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);
            messageInfoMap.put("city",userCity);
            groupMessageKeyReference.updateChildren(messageInfoMap).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                     //   Toast.makeText(context, "added to database", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                       // Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                                    }
                        }
                    }
            );
            userMessageInput.setText("");





        }
    }

    private void GetUserInfo() {


        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Toast.makeText(GroupChatActivity.this, "yes", Toast.LENGTH_LONG).show();

                    currentUserName = dataSnapshot.child("name").getValue().toString();
                    userCity = dataSnapshot.child("city").getValue().toString();
                    //if(TextUtils.isEmpty(currentUserName)){
                        //Toast.makeText(GroupChatActivity.this,currentUserName,Toast.LENGTH_LONG).show();
                    //}
                }
                else{
                    Toast.makeText(GroupChatActivity.this, "no", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void InitializeFields() {
        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);
        sendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput= findViewById(R.id.input_message);
       // displayTextMessage = findViewById(R.id.group_chat_text_display);
        //mScrollView = findViewById(R.id.scroll_view);
       // messageAdapter = new ReviewAdapter(messageList,context,currentGroupName);
        userMessageList = findViewById(R.id.group_recycler_view);
        userMessageList.setLayoutManager(new LinearLayoutManager(this));
        //linearLayoutManager = new LinearLayoutManager(this);
        //userMessageList.setLayoutManager(linearLayoutManager);
        //userMessageList.setAdapter(messageAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
//       GroupNameRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if(dataSnapshot.exists()){
//                    ReviewModal messages = dataSnapshot.getValue(ReviewModal.class);
//                    messageList.add(messages);
//                    // Messages a = messageList.get(0);
//
//                    // t.setText(a.getMessage());
//                    messageAdapter.notifyDataSetChanged();
//
//                    userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////                if(dataSnapshot.exists()){
////                    ReviewModal messages = dataSnapshot.getValue(ReviewModal.class);
////                    messageList.add(messages);
////                    // Messages a = messageList.get(0);
////
////                    // t.setText(a.getMessage());
////                    messageAdapter.notifyDataSetChanged();
////
////                    userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
//        //        }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<ReviewModal>()
                        .setQuery(GroupNameRef,ReviewModal.class)
                        .build();

        FirebaseRecyclerAdapter<ReviewModal, GroupChatActivity.groupViewHolder> adapter =
                new FirebaseRecyclerAdapter<ReviewModal, GroupChatActivity.groupViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final GroupChatActivity.groupViewHolder groupViewHolder, int i, @NonNull final ReviewModal reviewModal) {
                        final String usersId = getRef(i).getKey();

                       // final ReviewModal messages = UserMessageList.get(position);
                        String textImage;
                        String date = reviewModal.getDate();
                        String time = reviewModal.getTime();
                        String name = reviewModal.getName();
                        String city = reviewModal.getCity();
                        final String post = reviewModal.getPost();

                        groupViewHolder.dateTime.setText(date+" "+time);
                        groupViewHolder.post.setText(post);
                        groupViewHolder.name.setText(name);
                       groupViewHolder.city.setText(("Team:"+city).toString());
                        //String arr[] = new String[];
                       // StringBuilder stringBuilder = new StringBuilder(name);
                        //groupViewHolder.textImage.setText(stringBuilder);

                       // groupViewHolder.textImage.setText(name.charAt(0));
                        groupViewHolder.l2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent groupChatIntent = new Intent(GroupChatActivity.this,Comment.class);
                                groupChatIntent.putExtra("groupName",currentGroupName);
                                groupChatIntent.putExtra("groupKey",currentGroupId);
                                groupChatIntent.putExtra("messageKey",usersId);
                                groupChatIntent.putExtra("post",post);
                                startActivity(groupChatIntent);

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public GroupChatActivity.groupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review,parent,false);
                        GroupChatActivity.groupViewHolder viewHolder = new GroupChatActivity.groupViewHolder(view);
                        return viewHolder;
                    }
                };
        userMessageList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class groupViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTime,post,name,line,comment1,textImage,city;
        public LinearLayout l2;
        public ImageButton comment;


        public groupViewHolder(@NonNull View itemView) {
            super(itemView);
            textImage = itemView.findViewById(R.id.textButton);
            dateTime= itemView.findViewById(R.id.dateTime);
            name = itemView.findViewById(R.id.name);
            post = itemView.findViewById(R.id.post);
            line = itemView.findViewById(R.id.line);
            l2 = itemView.findViewById(R.id.l2);
            comment = itemView.findViewById(R.id.comment_button);
            comment1 = itemView.findViewById(R.id.comment_text1);
            city = itemView.findViewById(R.id.city);


        }
    }

  /*  private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String chatDate = (String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String)((DataSnapshot)iterator.next()).getValue();
            String chatName = (String)((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String)((DataSnapshot)iterator.next()).getValue();

            displayTextMessage.append(   chatName+chatMessage +"\n"+ chatTime+"   "+chatDate+"\n\n\n");
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }*/
}























