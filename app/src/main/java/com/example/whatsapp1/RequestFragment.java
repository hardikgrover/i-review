package com.example.whatsapp1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class RequestFragment extends Fragment {
    private View requestFragmentView;
    private RecyclerView myRequestList;
    private DatabaseReference chatRequestRef,userRef,contactsRef;
    private FirebaseAuth mAuth;
    String currentUserId;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        requestFragmentView =  inflater.inflate(R.layout.fragment_request, container, false);
        myRequestList = requestFragmentView.findViewById(R.id.chat_request_list);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRequestRef.child(currentUserId),Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,RequestViewHolder> adapter = new
                FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder requestViewHolder, int i, @NonNull Contacts contacts) {
                        requestViewHolder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                        requestViewHolder.itemView.findViewById(R.id.request_decline_button).setVisibility(View.VISIBLE);

                        final String list_user_id = getRef(i).getKey();

                        DatabaseReference getTypeRef = getRef(i).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    String type = dataSnapshot.getValue().toString();
                                    if(type.equals("recieved")){
                                        userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.hasChild("image")){
                                                    final String userImage = dataSnapshot.child("image").getValue().toString();


                                                    Picasso.get().load(userImage).into(requestViewHolder.userImage);

                                                }

                                                    final String userName = dataSnapshot.child("name").getValue().toString();
                                                    final String userStatus = dataSnapshot.child("status").getValue().toString();

                                                    requestViewHolder.userName.setText(userName);
                                                    requestViewHolder.userStatus.setText("wants to connect with you");

                                                requestViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        CharSequence options[] = new CharSequence[]
                                                                {
                                                                        "Accept",
                                                                        "Cancel"
                                                                };
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle(userName + "Chat Request");
                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                           if(i == 0){
                                                               contactsRef.child(currentUserId).child(list_user_id).child("Contacts")
                                                                       .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<Void> task) {
                                                                  if(task.isSuccessful()){
                                                                      contactsRef.child(list_user_id).child(currentUserId).child("Contacts")
                                                                              .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                          @Override
                                                                          public void onComplete(@NonNull Task<Void> task) {
                                                                              if(task.isSuccessful()){
                                                                                  chatRequestRef.child(currentUserId).child(list_user_id)
                                                                                          .removeValue()
                                                                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                              @Override
                                                                                              public void onComplete(@NonNull Task<Void> task) {
                                                                                             if(task.isSuccessful()){
                                                                                                 chatRequestRef.child(list_user_id).child(currentUserId)
                                                                                                         .removeValue()
                                                                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                             @Override
                                                                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                                                                 if(task.isSuccessful()){
                                                                                                                     Toast.makeText(getContext(), "contact saved", Toast.LENGTH_SHORT).show();
                                                                                                                 }
                                                                                                             }
                                                                                                         });
                                                                                             }
                                                                                              }
                                                                                          });

                                                                              }
                                                                          }
                                                                      });
                                                                  }
                                                                   }
                                                               });
                                                           }
                                                           else if(i == 1){
                                                               chatRequestRef.child(currentUserId).child(list_user_id)
                                                                       .removeValue()
                                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                               if(task.isSuccessful()){
                                                                                   chatRequestRef.child(list_user_id).child(currentUserId)
                                                                                           .removeValue()
                                                                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                               @Override
                                                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                                                   if(task.isSuccessful()){
                                                                                                       Toast.makeText(getContext(), "request removed", Toast.LENGTH_SHORT).show();
                                                                                                   }
                                                                                               }
                                                                                           });
                                                                               }
                                                                           }
                                                                       });



                                                                }

                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

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
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                           RequestViewHolder holder= new RequestViewHolder(view);
                           return holder;
                    }
                };
        myRequestList.setAdapter(adapter);
        adapter.startListening();


    }
    public static class RequestViewHolder extends RecyclerView.ViewHolder{
            TextView userName,userStatus;
            ImageView userImage;
            Button acceptButton,CancelButton;
        public RequestViewHolder(@NonNull View itemView) {

            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_profile);
            userStatus = itemView.findViewById(R.id.user_status);
            acceptButton = itemView.findViewById(R.id.request_accept_button);
            CancelButton = itemView.findViewById(R.id.request_decline_button);
        }
    }
}
