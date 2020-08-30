package com.example.whatsapp1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
   // private View groupFragmentView;
    private View groupFragmentView;
    private RecyclerView myGroupList;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList = new ArrayList<>();
    private DatabaseReference groupRef;
    private Context context ;
    private ImageView groupBackground;


    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView =  inflater.inflate(R.layout.fragment_group, container, false);

        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        myGroupList = groupFragmentView.findViewById(R.id.group_view);
        myGroupList.setLayoutManager(new LinearLayoutManager(getContext()));
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
      //  RetrieveAndDisplayGroups();
     //   list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
       //     @Override
         //   public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
           //     String CurrentGroupName = adapterView.getItemAtPosition(position).toString();
             //   Intent groupChatIntent = new Intent(getContext(),GroupChatActivity.class);
               //groupChatIntent.putExtra("groupName",CurrentGroupName);
                //startActivity(groupChatIntent);
            //}
        //});
        return groupFragmentView;

    }

    @Override
    public void onStart() {
        //Toast.makeText(getActivity(), "go", Toast.LENGTH_SHORT).show();
        super.onStart();
       FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<group>()
               .setQuery(groupRef,group.class)
                .build();

        FirebaseRecyclerAdapter<group,groupViewHolder>adapter =
                new FirebaseRecyclerAdapter<group, groupViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final groupViewHolder groupViewHolder, int i, @NonNull final group group) {
                        final String usersId = getRef(i).getKey();

                        final String groupName = group.getGroupName();
                        groupViewHolder.groupName.setText(groupName);
                        groupViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent groupChatIntent = new Intent(getContext(),GroupChatActivity.class);
                                groupChatIntent.putExtra("groupName",groupName);
                                groupChatIntent.putExtra("groupKey",usersId);
                                startActivity(groupChatIntent);

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public groupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_view,parent,false);
                            groupViewHolder viewHolder = new groupViewHolder(view);
                            return viewHolder;
                    }
                };
        myGroupList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class groupViewHolder extends RecyclerView.ViewHolder {
    private TextView groupName,groupImage;
    //private ImageView groupBackground;
        public groupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.fragment_group_name);
            groupImage = itemView.findViewById(R.id.group_logo);

        }
    }
//       private void RetrieveAndDisplayGroups() {
//        groupRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange( DataSnapshot dataSnapshot) {
//                Set<String> set = new HashSet<>();
//                Iterator iterator = dataSnapshot.getChildren().iterator();
//                while (iterator.hasNext()){
//                     set.add(((DataSnapshot)iterator.next()).getKey());
//                 }
//
//                 arrayList.clear();
//               arrayList.addAll(set);
//                 arrayAdapter.addAll(set);
//                 arrayAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled( DatabaseError databaseError) {
//
//            }
//        });
//    }


}
