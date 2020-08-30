package com.example.whatsapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
private Toolbar mToolbar;
private ViewPager myViewPager;
private TabLayout myTabLayout;
private TabAccessorAdapter myTabAccessorAdapter;
private FirebaseAuth mauth;
private DatabaseReference rootRef;
private String currentUserId;
private ImageView groupBackground;
private TextView start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootRef = FirebaseDatabase.getInstance().getReference();
        mauth = FirebaseAuth.getInstance();
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("whatsapp");

        myViewPager = findViewById(R.id.main_tabs_pager);
        myTabAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);
        myTabLayout  = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mauth.getCurrentUser();
        if (currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else{
            UpdateUserStatus("online");
            VerfyUserExistence();
        }
    }

    @Override
    protected void onStop() {
        FirebaseUser currentUser = mauth.getCurrentUser();

        super.onStop();
       // Toast.makeText(this, "on Stop", Toast.LENGTH_SHORT).show();
                if(currentUser != null) {
                    UpdateUserStatus("offline");
                }



    }

    @Override
    protected void onDestroy() {
        FirebaseUser currentUser = mauth.getCurrentUser();


        super.onDestroy();
        Toast.makeText(this, "on Destroy ", Toast.LENGTH_SHORT).show();
        if (currentUser != null) {
            UpdateUserStatus("offline");
        }




    }

    private void VerfyUserExistence() {
        String currentUserId  = mauth.getCurrentUser().getUid();
       rootRef.child("users").child(currentUserId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange( DataSnapshot dataSnapshot) {
          if((dataSnapshot.child("name").exists())){
                   //Toast.makeText(MainActivity.this,"welcome",Toast.LENGTH_SHORT).show();
               }
          else{
             // Toast.makeText(MainActivity.this,"error",Toast.LENGTH_SHORT).show();

              SendUserToSettingsActivity();
          }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(loginIntent);
        finish();
    }
    private void SendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this,SettngsActivity.class);

        startActivity(settingsIntent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout_option){
            UpdateUserStatus("offline");
            mauth.signOut();
            SendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.main_settings_option){
            SendUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.main_find_friends_option){
            SendUsertoFindFreindsActivity();
        }
        if (item.getItemId() == R.id.create_group_option){
        RequestNewGroup();
        }
        return true;
    }

    private void SendUsertoFindFreindsActivity() {
        Intent Intent = new Intent(MainActivity.this,FindFriends.class);


        startActivity(Intent);

    }


    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("enter the group name");
         final EditText groupNameField = new EditText(MainActivity.this);
         groupNameField.setHint("e.g coding cafe");
         builder.setView(groupNameField);
         builder.setPositiveButton("create", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                String input = groupNameField.getText().toString();
                if(TextUtils.isEmpty(input)){
                    Toast.makeText(MainActivity.this, "please write group name", Toast.LENGTH_SHORT).show();
                }
                else{
                    CreateNewGroup(input);

                }
             }
         });
         builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
             }
         });
         builder.show();
    }

    private void CreateNewGroup( final String input) {
        groupBackground = findViewById(R.id.group_image);
        groupBackground.setVisibility(View.INVISIBLE);
        start = findViewById(R.id.start_team_chat);
        start.setVisibility(View.INVISIBLE);

        String key = rootRef.push().getKey();
        rootRef.child("Groups").child(key).child("groupName").setValue(input)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                           // Toast.makeText(MainActivity.this,  input+" is created successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void UpdateUserStatus(String state){
        String saveCurrentTime,saveCurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hhh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String,Object> onLineState = new HashMap<>();
        onLineState.put("time",saveCurrentTime);
        onLineState.put("date",saveCurrentDate);
        onLineState.put("state",state);
        currentUserId = mauth.getCurrentUser().getUid();

        rootRef.child("users").child(currentUserId).child("userState")
                .updateChildren(onLineState);



    }
}
