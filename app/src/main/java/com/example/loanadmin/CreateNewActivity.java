package com.example.loanadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CreateNewActivity extends AppCompatActivity {

    private EditText memberId,phoneNumber,password;
    private Spinner memberTypeSpinner;

    private TextView createNew, logIn;

    private String[] membersType = {"SuperAdmin", "Admin", "SuperUser","User"};
    private String typeSelected = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);

        initUi();
    }

    private void initUi() {
        memberId = findViewById(R.id.memberIdForReg);
        phoneNumber = findViewById(R.id.phoneNumberForReg);
        password = findViewById(R.id.passwordForReg);
        createNew = findViewById(R.id.createText);
        logIn = findViewById(R.id.logInTextForReg);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateNewActivity.this,LogIn.class));
            }
        });

        createNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });


        memberTypeSpinner = findViewById(R.id.memberTypeSpinnerForReg);

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

    }

    private void checkValidation() {
        if (TextUtils.isEmpty(memberId.getText().toString())) {
            memberId.setError("Id can't be empty");
            memberId.requestFocus();
            return;
        }

        if (memberId.getText().toString().length() < 2) {
            memberId.setError("Member id should be at least 2 digit!");
            memberId.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Password can't be empty");
            password.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
            phoneNumber.setError("Please enter valid Phone Number");
            phoneNumber.requestFocus();
            return;
        }

        if (phoneNumber.getText().toString().length() > 14 ||
                phoneNumber.getText().toString().length() < 13) {
            phoneNumber.setError("Phone number should be valid");
            phoneNumber.requestFocus();
            return;
        }

        String mId = memberId.getText().toString();
        String pass = password.getText().toString();
        String phone = phoneNumber.getText().toString();

        String id = UUID.randomUUID().toString();
        AdminModel model = new AdminModel(
                id,
                mId,
                pass,
                typeSelected,
                phone
        );

        Common.adminRef.child(id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CreateNewActivity.this, "Created Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(CreateNewActivity.this, "Failed To Create", Toast.LENGTH_SHORT).show();
            }
        });

    }
}