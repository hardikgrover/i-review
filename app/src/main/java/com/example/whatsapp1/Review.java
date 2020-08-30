package com.example.whatsapp1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Review extends Fragment {
    private EditText reviewComment;
    private View view;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef,userRef,groupMessageKeyReference;
    private ImageButton reviewButton;
    String senderUserId ;
    private final List<ReviewModal> messageList = new ArrayList<>();
    private String currentUserName,currentDate,currentTime;




    public Review() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
         view =  inflater.inflate(R.layout.fragment_review, container, false);
        InitializeFields();
        mRef = FirebaseDatabase.getInstance().getReference().child("review");
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        senderUserId = mAuth.getCurrentUser().getUid();
        GetUserInfo();
        reviewButton.setOnClickListener(new View.OnClickListener() {
          @Override
        public void onClick(View view) {
          sendMessage();
              reviewComment.setText("");

          }
        });


        return view;
    }

    private void sendMessage() {
        String messageKey = mRef.push().getKey();

        String Comment = reviewComment.getText().toString();
        if(TextUtils.isEmpty(Comment)){
            //Toast.makeText(Review, "please enter message", Toast.LENGTH_SHORT).show();

        }
        else{
            //mRef.setValue("");
            Calendar callForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM  dd  yyyy" );
            currentDate = currentDateFormat.format(callForDate.getTime());
            Calendar callForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh mm a" );
            currentTime = currentTimeFormat.format(callForTime.getTime());

            HashMap<String,Object> groupMessageKey = new HashMap<>();
            mRef.updateChildren(groupMessageKey);

            groupMessageKeyReference = mRef.child(messageKey);

            HashMap<String,Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",reviewComment);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);
            //groupMessageKeyReference.updateChildren(messageInfoMap);
                mRef.updateChildren(messageInfoMap);
        }
    }

   /* @Override
    public void onStart() {
        super.onStart();

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               // ReviewModal messages = dataSnapshot.getValue(ReviewModal.class);
                //messageList.add(messages);
                // Messages a = messageList.get(0);

                // t.setText(a.getMessage());
                //messageAdapter.notifyDataSetChanged();

                //userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
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
    }*/

    private void InitializeFields() {
        reviewComment = view.findViewById(R.id.review_text);
        recyclerView = view.findViewById(R.id.review_list);
        reviewButton = view.findViewById(R.id.review_button);
    }
    private void GetUserInfo() {


        userRef.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Toast.makeText(GroupChatActivity.this, "yes", Toast.LENGTH_LONG).show();

                    currentUserName = dataSnapshot.getValue().toString();
                    //if(TextUtils.isEmpty(currentUserName)){
                    //Toast.makeText(GroupChatActivity.this,currentUserName,Toast.LENGTH_LONG).show();
                    //}
                }
                else{
                    //Toast.makeText(Review., "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
