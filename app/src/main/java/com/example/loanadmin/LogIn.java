package com.example.loanadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loanadmin.Common.Common;
import com.example.loanadmin.Model.AdminModel;
import com.facebook.login.Login;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import static com.example.loanadmin.Common.Common.DEFAULT;

public class LogIn extends AppCompatActivity {

    private EditText memberId, password;
    private TextView signIn, createOne, forgotPassword;
    private Spinner memberTypeSpinner;

    private String[] membersType = {"SuperAdmin", "Admin", "SuperUser","User"};
    private String typeSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initUI();
    }

    private void initUI() {
        memberId = findViewById(R.id.memberIdLogIn);
        password = findViewById(R.id.passwordLogIn);
        signIn = findViewById(R.id.signInNowText);
        createOne = findViewById(R.id.createNewText);
        forgotPassword = findViewById(R.id.forgotPasswordText);
        memberTypeSpinner = findViewById(R.id.memberTypeSpinner);

        ArrayAdapter<String> memberAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, membersType);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        memberTypeSpinner.setAdapter(memberAdapter);

        memberTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(memberId.getText().toString())) {
                    memberId.setError("Please fill the id");
                    memberId.requestFocus();
                    return;
                }

                startActivity(new Intent(LogIn.this, ForgetPhoneNumberActivity.class)
                        .putExtra("memberId", memberId.getText().toString()));
            }
        });

        createOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogIn.this, CreateNewActivity.class));
            }
        });


    }

    private void checkValidation() {

        if (TextUtils.isEmpty(memberId.getText().toString())) {
            memberId.setError("Id can't be empty");
            memberId.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Password can't be empty");
            password.requestFocus();
            return;
        }

        String mId = memberId.getText().toString();
        String pass = password.getText().toString();
        Common.adminRef.orderByChild("memberId").equalTo(mId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AdminModel model = snapshot.getValue(AdminModel.class);

                        if (model.getMemberId().equals(mId)
                                && model.getMemberType().equals(typeSelected)
                                && model.getPassword().equals(pass)) {

                            saveDataOnInternalStorage(mId, pass);

                            Toast.makeText(LogIn.this, "Success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LogIn.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LogIn.this, "Invalid Credential", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(LogIn.this, "Admin Doesn't exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(LogIn.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveDataOnInternalStorage(String id, String pass) {

        SharedPreferences sharedPreference = getSharedPreferences("LogIN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();

        editor.putString("id", id);
        editor.putString("pass", pass);
        editor.commit();

        Common.userIdForNotification = sharedPreference.getString("id", DEFAULT);
        Common.pass = sharedPreference.getString("pass", DEFAULT);
    }
}