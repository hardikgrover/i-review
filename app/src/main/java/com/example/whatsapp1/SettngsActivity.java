package com.example.whatsapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettngsActivity extends AppCompatActivity {
    private Button updateAccountSettings;
    private EditText userName,userStatus,userCity;
    private CircleImageView userProfileImage;
    private String currentUid;
    private FirebaseAuth mauth;
    private DatabaseReference rootRef;
    private static int galleryPick = 1;
    private StorageReference userProfileImagesRef;
    private ProgressDialog loadingBar;
    private Toolbar settingsToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settngs);
        mauth = FirebaseAuth.getInstance();
        currentUid = mauth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("profile images");
        loadingBar = new ProgressDialog(this);

        IntializeFields();
       // userName.setVisibility(View.INVISIBLE);
        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSettings();

            }
        });
        RetrieveUserInformation();
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,galleryPick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == galleryPick) && resultCode == RESULT_OK && data != null) {
            Uri uriClass = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("set profile image");
                loadingBar.setMessage("please wait while your profile image is uploading");
                loadingBar.setCanceledOnTouchOutside(false)
                ;
                //loadingBar.show();
                Uri resultUri = result.getUri();
                StorageReference filePath = userProfileImagesRef.child(currentUid + ".jpg");


                filePath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUrl = uri.toString();

                                        rootRef.child("users").child(currentUid).child("image")
                                                .setValue(downloadUrl)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(SettngsActivity.this, "Image saved in database successfuly", Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();
                                                        } else {
                                                            String message = task.getException().toString();
                                                            Toast.makeText(SettngsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();

                                                        }

                                                    }
                                                });

                                    }
                                });

                            }
                        });

            }
        }
    }
    private void RetrieveUserInformation() {

   rootRef.child("users").child(currentUid)
           .addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if( (dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))){
                    String retreiveUserName = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                    String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();
                    String retrieveCity = dataSnapshot.child("city").getValue().toString();
                    userName.setText(retreiveUserName);
                    userStatus.setText(retrieveStatus);
                    Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                    userCity.setText(retrieveCity);

                }
                else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") )) {
                    String retreiveUserName = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                    String retrieveCity = dataSnapshot.child("city").getValue().toString();

                    userName.setText(retreiveUserName);
                    userStatus.setText(retrieveStatus);
                    userCity.setText(retrieveCity);
                }
                else{
                   // userName.setVisibility(View.VISIBLE);
                    Toast.makeText(SettngsActivity.this, "please update your profile information", Toast.LENGTH_SHORT).show();

                }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
    }

    private void UpdateSettings() {

        String setUser = userName.getText().toString();
        String setStatus = userStatus.getText().toString();
        String setUserCity = userCity.getText().toString();

        if(( TextUtils.isEmpty(setUser))){
               Toast.makeText(SettngsActivity.this,"please write username",Toast.LENGTH_SHORT).show();


        }
         else if(TextUtils.isEmpty(setStatus)){
            Toast.makeText(SettngsActivity.this,"please write your status",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(setUserCity)){
            Toast.makeText(SettngsActivity.this,"please write your city",Toast.LENGTH_SHORT).show();
        }
        else{
             Toast.makeText(SettngsActivity.this,"added",Toast.LENGTH_SHORT).show();

            HashMap<String,Object> profileMap = new HashMap<>();
            profileMap.put("uid",currentUid);
            profileMap.put("name",setUser);
            profileMap.put("status",setStatus);
            profileMap.put("city",setUserCity);

            rootRef.child("users").child(currentUid).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(SettngsActivity.this," profile updated successfully",Toast.LENGTH_SHORT).show();


                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(SettngsActivity.this,"error:"+message,Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


            }
    }

    private void IntializeFields() {
        updateAccountSettings = findViewById(R.id.update_settings_button);
        userName = findViewById(R.id.set_user_name);
        userStatus = findViewById(R.id.set_profile_status);
        userProfileImage = findViewById(R.id.profile_image);
        userCity = findViewById(R.id.set_user_city);
        settingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(" Account Settings");
    }

    private void SendUserToMainActivity() {
        Intent intent  = new Intent(SettngsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
