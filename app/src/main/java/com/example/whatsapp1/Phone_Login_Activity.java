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
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class Phone_Login_Activity extends AppCompatActivity {
    private Button sendVerificationCode,Verify;
    private EditText InputPhoneNumber,InputVerificationCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone__login_);
        mAuth = FirebaseAuth.getInstance();
        sendVerificationCode = findViewById(R.id.send_verifacation_code);
        Verify = findViewById(R.id.verify);
        InputPhoneNumber = findViewById(R.id.phone_number_login);
        InputVerificationCode = findViewById(R.id.verify_phone_number);
        loadingBar = new ProgressDialog(this);


        sendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String phoneNumber = InputPhoneNumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(Phone_Login_Activity.this, "phone number is recquired", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("phone verificaton");
                    loadingBar.setMessage("please wait we are getting your credintials");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            Phone_Login_Activity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks


                }
            }
        });
        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);

                String verficationCode = InputVerificationCode.getText().toString();
                if(TextUtils.isEmpty(verficationCode)){
                    Toast.makeText(Phone_Login_Activity.this, "please write code first", Toast.LENGTH_SHORT).show();
                }
                else{
                    loadingBar.setTitle("verification code ");
                    loadingBar.setMessage("please wait we are verifying your code");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verficationCode );

                    signInWithPhoneAuthCredential(credential);
                }

            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                //Toast.makeText(Phone_Login_Activity.this, "hi", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingBar.dismiss();

                    String a =e.getMessage();
                Toast.makeText(Phone_Login_Activity.this, a, Toast.LENGTH_LONG).show();
                sendVerificationCode.setVisibility(View.VISIBLE);
                InputPhoneNumber.setVisibility(View.VISIBLE);

                Verify.setVisibility(View.INVISIBLE);
                InputVerificationCode.setVisibility(View.INVISIBLE);
            }
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                loadingBar.dismiss();
                Toast.makeText(Phone_Login_Activity.this, "verification code sent to your mobile number", Toast.LENGTH_SHORT).show();
                sendVerificationCode.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);

                Verify.setVisibility(View.VISIBLE);
                InputVerificationCode.setVisibility(View.VISIBLE);
            }

            };

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                                loadingBar.dismiss();
                            Toast.makeText(Phone_Login_Activity.this, "you are logged in successfully !", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(Phone_Login_Activity.this, "error:"+error, Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent intent = new Intent(Phone_Login_Activity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


}
