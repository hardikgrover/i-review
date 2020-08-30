package com.example.whatsapp1;

import android.media.Image;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactsFragments extends Fragment {
    private View contactsView;
    private RecyclerView mContactsList;
    private DatabaseReference contactRef,userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public ContactsFragments() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contactsView =  inflater.inflate(R.layout.fragment_contacts_fragments, container, false);
        mContactsList  = contactsView.findViewById(R.id.contacts_list);
        mContactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        return  contactsView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactRef,Contacts.class)
                .build();


       final FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder contactsViewHolder, int i, @NonNull Contacts contacts) {
                String userId = getRef(i).getKey();
                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            if (dataSnapshot.child("userState").hasChild("state")){
                                String state = dataSnapshot.child("userState").child("state").getValue().toString();

                                if (state.equals("online")){
                                    contactsViewHolder.onlineIcon.setVisibility(View.VISIBLE);
                                }
                                else if (state.equals("offline")){
                                    contactsViewHolder.onlineIcon.setVisibility(View.INVISIBLE);

                                }
                            }
                            else{
                                contactsViewHolder.onlineIcon.setVisibility(View.INVISIBLE);


                            }
                            if(dataSnapshot.hasChild("image")){
                                String profileImage = dataSnapshot.child("image").getValue().toString();
                                String profileName = dataSnapshot.child("name").getValue().toString();
                                String profileStatus = dataSnapshot.child("status").getValue().toString();
                               // String onlineStatus = dataSnapshot.child("userStatus").child("state").getValue().toString();

                                contactsViewHolder.userName.setText(profileName);
                                contactsViewHolder.userStatus.setText(profileStatus);
                                Picasso.get().load(profileImage).into(contactsViewHolder.userImage);
                            }
                            else{
                                String profileName = dataSnapshot.child("name").getValue().toString();
                                String profileStatus = dataSnapshot.child("status").getValue().toString();

                                contactsViewHolder.userName.setText(profileName);
                                contactsViewHolder.userStatus.setText(profileStatus);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };
       mContactsList.setAdapter(adapter);
       adapter.startListening();
    }
    public static class ContactsViewHolder extends RecyclerView.ViewHolder{
        TextView userName,userStatus;
        CircleImageView userImage;
        ImageView onlineIcon;


        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile);
            userStatus = itemView.findViewById(R.id.user_status);
            userImage = itemView.findViewById(R.id.circular_imageView);
            onlineIcon = itemView.findViewById(R.id.user_image);
        }
    }
}

















