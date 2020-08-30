package com.example.whatsapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton,phoneLoginButton;
    private EditText userEmail,userPassword;
    private TextView needNewAccountLink,forgetPasswordLink;
    private FirebaseAuth mauth;
    private ProgressDialog  loadingBar;
    private DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mauth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        InitializeFields();
        phoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,Phone_Login_Activity.class);
                startActivity(intent);
            }
        });
        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendUserToRegisterActivity();
               // Toast.makeText(LoginActivity.this,"hi",Toast.LENGTH_SHORT).show();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             AllowUserToLogin();
            }
        });
    }

    private void AllowUserToLogin() {
        String email = userEmail.getText().toString();
        String password  = userPassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"please enter email",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"please enter password",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("logging in");
            loadingBar.setMessage("please wait while we are logging you in");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mauth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           String currentUserId = mauth.getCurrentUser().getUid();
                           String deviceToken = FirebaseInstanceId.getInstance().getToken();
                          // usersRef.child(currentUserId).child("device_token").setValue(deviceToken).addOnCompleteListener(
                            //       new OnCompleteListener<Void>() {
                              //         @Override
                                //       public void onComplete(@NonNull Task<Void> task) {
                                  //         if (task.isSuccessful()){
                                    //           Toast.makeText(LoginActivity.this,"login successful",Toast.LENGTH_SHORT).show();
                                      //         loadingBar.dismiss();
                                               SendUserToMainActivity();

//                                           }
  //                                     }
    //                               }
      //                     );





                       }
                       else {
                           String message = task.getException().toString();
                           Toast.makeText(LoginActivity.this,"error"+message,Toast.LENGTH_SHORT).show();
                           loadingBar.dismiss();
                       }
                        }
                    });

        }
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);

    }

    private void InitializeFields() {
        loginButton = findViewById(R.id.login_button);
        phoneLoginButton = findViewById(R.id.login_phone);
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        needNewAccountLink = findViewById(R.id.need_account);
        forgetPasswordLink = findViewById(R.id.forget_password);
        loadingBar = new ProgressDialog(this);

    }



    private void SendUserToMainActivity() {
        Intent intent  = new Intent(LoginActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
