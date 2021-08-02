package com.example.loanadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class ForgotPassword extends AppCompatActivity {


    private EditText firstCode, secondCode, thirdCode, fourthCode, fifthCode, sixthCode;
    private TextView resendCode, verificationOTP;
    private static String otp, givenOTP;

    private FirebaseAuth userAuth;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private String memberId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        userAuth = FirebaseAuth.getInstance();
//        if (getIntent() != null) {
//            phone = getIntent().getStringExtra("phone");
//        }
        if (getIntent() != null) {
            otp = getIntent().getStringExtra("otp");
            memberId = getIntent().getStringExtra("memberId");
        }

        initUI();
    }

    private void initUI() {
        firstCode = findViewById(R.id.firstCode);
        secondCode = findViewById(R.id.secondCode);
        thirdCode = findViewById(R.id.thirdCode);
        fourthCode = findViewById(R.id.fourthCode);
        fifthCode = findViewById(R.id.fifthCode);
        sixthCode = findViewById(R.id.sixthCode);
        resendCode = findViewById(R.id.resendText);
        verificationOTP = findViewById(R.id.verifyOtp);


        verificationOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

        numberOTPMOve();

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP();
            }
        });
    }

    private void resendOTP() {
        startActivity(new Intent(ForgotPassword.this, LogIn.class));

//        PhoneAuthOptions options =
//                PhoneAuthOptions.newBuilder(userAuth)
//                        .setPhoneNumber(phone)       // Phone number to verify
//                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//                        .setActivity(this)                 // Activity (for callback binding)
//                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                            @Override
//                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//
//                            }
//
//                            @Override
//                            public void onVerificationFailed(@NonNull FirebaseException e) {
//
//                            }
//
//                            @Override
//                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                otp = s;
//                            }
//                        })
//                        // OnVerificationStateChangedCallbacks
//                        .build();
//        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void numberOTPMOve() {
        firstCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().trim().isEmpty()) {
                    secondCode.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        secondCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().trim().isEmpty()) {
                    thirdCode.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        thirdCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().trim().isEmpty()) {
                    fourthCode.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fourthCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().trim().isEmpty()) {
                    fifthCode.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fifthCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().trim().isEmpty()) {
                    sixthCode.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void validation() {

        if (!TextUtils.isEmpty(firstCode.getText().toString().trim())
                && !TextUtils.isEmpty(secondCode.getText().toString().trim())
                && !TextUtils.isEmpty(thirdCode.getText().toString().trim())
                && !TextUtils.isEmpty(fourthCode.getText().toString().trim())
                && !TextUtils.isEmpty(fifthCode.getText().toString().trim())
                && !TextUtils.isEmpty(sixthCode.getText().toString().trim())
        ) {
            givenOTP = firstCode.getText().toString() + secondCode.getText().toString() + thirdCode.getText().toString() +
                    fourthCode.getText().toString() + fifthCode.getText().toString() + sixthCode.getText().toString();


            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otp, givenOTP);
            if (givenOTP.equals(credential.getSmsCode())) {
                //Toast.makeText(this, "Matched", Toast.LENGTH_SHORT).show();

                //Create Sign up options:
                resetPassword();

            } else {
                Toast.makeText(this, "not matched", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetPassword() {
        startActivity(new Intent(this, ResetPasswordActivity.class).putExtra("memberId", memberId));
        finish();
    }
}