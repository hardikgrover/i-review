package com.example.whatsapp1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragments extends Fragment {
    private View PrivateChatsView;
    private RecyclerView chatList;
    private DatabaseReference chatsRef,usersRef;
    private String currentUserId;
private FirebaseAuth mAuth;

    public ChatsFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateChatsView =  inflater.inflate(R.layout.fragment_chats_fragments, container, false);

            chatList = PrivateChatsView.findViewById(R.id.chat_list);
            chatList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
            chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
            usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        return PrivateChatsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatsRef,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder chatsViewHolder, int i, @NonNull Contacts contacts) {
                        final String usersId = getRef(i).getKey();
                        final String[] retImage = {"default_image"};
                        usersRef.child(usersId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               if(dataSnapshot.exists()){
                                   if(dataSnapshot.hasChild("image")){
                                         retImage[0] = dataSnapshot.child("image").getValue().toString();
                                       Picasso.get().load(retImage[0]).into(chatsViewHolder.userImage);
                                   }
                                   final String userName = dataSnapshot.child("name").getValue().toString();
                                   final String userStatus = dataSnapshot.child("status").getValue().toString();


                                   chatsViewHolder.userName.setText(userName);
                                   if (dataSnapshot.child("userState").hasChild("state")){
                                       String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                       String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                       String time = dataSnapshot.child("userState").child("time").getValue().toString();
                                       if (state.equals("online")){
                                           chatsViewHolder.userStatus.setText("online");
                                       }
                                       if (state.equals("offline")){
                                           chatsViewHolder.userStatus.setText("Last seen:"+"\n"+date+"  "+time);

                                       }
                                   }
                                   else{
                                       chatsViewHolder.userStatus.setText("offline");


                                   }

                                   chatsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           Intent intent = new Intent(getContext(),ChatActivity.class);
                                           intent.putExtra("visit_user_id",usersId);
                                           intent.putExtra("visit_name",userName);
                                           intent.putExtra("visit_image", retImage[0]);

                                           startActivity(intent);
                                       }
                                   });
                               }



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                        return new ChatsViewHolder(view);
                    }
                };
        chatList.setAdapter(adapter);
        adapter.startListening();

    }
    public static class ChatsViewHolder extends RecyclerView.ViewHolder{
        TextView userName,userStatus;
        ImageView userImage;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile);
            userStatus = itemView.findViewById(R.id.user_status);
            userImage = itemView.findViewById(R.id.circular_imageView);
        }
    }
}




















