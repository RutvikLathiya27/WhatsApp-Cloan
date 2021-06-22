package com.example.whatsappclone.view.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ActivityPhoneLoginActvityBinding;
import com.example.whatsappclone.model.user.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActvityActivity extends AppCompatActivity {

    private ActivityPhoneLoginActvityBinding binding;

    private static String TAG = "PhoneLoginActvityActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    private String mVarificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_login_actvity);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            startActivity(new Intent(this, SetUserInfoActivity.class));

        }

        progressDialog = new ProgressDialog(this);

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btnNext.getText().toString().equals("Next")) {
                    progressDialog.setMessage("Please wait");
                    progressDialog.show();

                    String phone = "+" + binding.edCodeCountry.getText().toString() + binding.edPhone.getText().toString();

                    startPhoneNumberVerification(phone);
                } else {

                    progressDialog.setMessage("Verifying.. ");
                    progressDialog.show();

                    verifyPhoneNumberWithCode(mVarificationId, binding.edCode.getText().toString());
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted: complete");
                signInWithPhoneAuthCredential(phoneAuthCredential);
                progressDialog.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(TAG, "onVerificationFailed: " + e.getMessage());
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                Log.d(TAG, "onCodeSent:" + verificationId);

                mVarificationId = verificationId;
                mResendToken = forceResendingToken;

                binding.btnNext.setText("Confirm");

                binding.edCode.setVisibility(View.VISIBLE);
                binding.edCodeCountry.setEnabled(false);
                binding.edPhone.setEnabled(false);

                progressDialog.dismiss();


            }

        };
    }

    private void startPhoneNumberVerification(String phoneNumber) {

        progressDialog.setMessage("Send code to : " + phoneNumber);
        progressDialog.show();

        //start auth
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        //end auth

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        //start verification with code
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        //end verification with code
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            progressDialog.dismiss();
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();

//                            startActivity(new Intent(PhoneLoginActvityActivity.this, SetUserInfoActivity.class));

                            if (user != null) {
                                String userId = user.getUid();

                                Users users = new Users(userId,
                                        "",
                                        user.getPhoneNumber(),
                                        "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        "");

                                firestore.collection("Users").document(userId)
                                        .set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        startActivity(new Intent(PhoneLoginActvityActivity.this, SetUserInfoActivity.class));
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                Toast.makeText(PhoneLoginActvityActivity.this, "Something Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        } else {

                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }

}