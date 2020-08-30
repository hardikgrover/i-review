package com.example.whatsapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {
    private Button createAccountButton;
    private EditText userEmail,userPassword;
    private TextView alreadyHaveAccountLink;
    private FirebaseAuth mauth;
    private ProgressDialog loadingBar;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        rootRef = FirebaseDatabase.getInstance().getReference();
        
        InializeFields();
        alreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToLoginActivity();

            }
        });
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
        mauth = FirebaseAuth.getInstance();
    }

     private void CreateNewAccount() {
        String email = userEmail.getText().toString();
        String password  = userPassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"please enter email",Toast.LENGTH_SHORT).show();
        }
       else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"please enter password",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("creating new account");
            loadingBar.setMessage("please while we are creating new account for you");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mauth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                String currentUserId = mauth.getCurrentUser().getUid();
                               // rootRef.child("users").child(currentUserId).child("device_token")
                                 //       .setValue(deviceToken);



                                rootRef.child("users").child(currentUserId).setValue("");
                                SendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this,"user created",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"error : "+message ,Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            }
                        }
                    });
        }

    }

    private void SendUserToMainActivity() {
        Intent intent  = new Intent(RegisterActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void InializeFields() {
        createAccountButton = findViewById(R.id.register_button);
        userEmail = findViewById(R.id.register_email);
        userPassword = findViewById(R.id.register_password);
        alreadyHaveAccountLink = findViewById(R.id.have_account);
        loadingBar = new ProgressDialog(this);

    }
    private void SendUserToLoginActivity() {
        Intent registerIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(registerIntent);
    }


}
