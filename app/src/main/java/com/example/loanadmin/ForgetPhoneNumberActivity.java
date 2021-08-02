package com.example.loanadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loanadmin.Common.Common;
import com.example.loanadmin.Model.AdminModel;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ForgetPhoneNumberActivity extends AppCompatActivity {

    private String memberId = "";

    private EditText phoneNumber;
    private TextView getOTP;

    private FirebaseAuth userAuth;
    private DatabaseReference userRef;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_phone_number);
        userAuth = FirebaseAuth.getInstance();

        if (getIntent() != null) {
            memberId = getIntent().getStringExtra("memberId");
        }

        phoneCallback();

        initUI();

    }

    private void initUI() {
        phoneNumber = findViewById(R.id.phoneNumberForForgetPassword);
        getOTP = findViewById(R.id.getOTPForForgetPassword);

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Common.adminRef.orderByChild("memberId").equalTo(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                AdminModel model = snapshot.getValue(AdminModel.class);
                                String p = phoneNumber.getText().toString();

                                if (p.equals(model.getPhone())) {
                                    PhoneAuthOptions options =
                                            PhoneAuthOptions.newBuilder(userAuth)
                                                    .setPhoneNumber(phoneNumber.getText().toString().trim())       // Phone number to verify
                                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                    .setActivity(ForgetPhoneNumberActivity.this)                 // Activity (for callback binding)
                                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                                    .build();
                                    PhoneAuthProvider.verifyPhoneNumber(options);
                                } else {
                                    Toast.makeText(ForgetPhoneNumberActivity.this, "Phone Number doesn't match", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(ForgetPhoneNumberActivity.this, "Invalid Data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void phoneCallback() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                // Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                //Log.w(TAG, "onVerificationFailed", e);

                Log.d("TEST_RESULT", ""+e.getMessage());

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                Intent intent = new Intent(ForgetPhoneNumberActivity.this, ForgotPassword.class);
                intent.putExtra("otp", verificationId);
                intent.putExtra("memberId", memberId);
                startActivity(intent);

            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {


    }
}