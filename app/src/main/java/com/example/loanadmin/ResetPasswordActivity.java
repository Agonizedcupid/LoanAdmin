package com.example.loanadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loanadmin.Common.Common;
import com.example.loanadmin.Model.AdminModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class ResetPasswordActivity extends AppCompatActivity {

    private String memberId = "";

    private EditText firstPassword,confirmPassword;
    private TextView savePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        if (getIntent() != null) {
            memberId = getIntent().getStringExtra("memberId");
        }
        initUI();

    }

    private void initUI() {
        firstPassword = findViewById(R.id.firstPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        savePassword = findViewById(R.id.savePassword);


        savePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstPassword.getText().toString().equals(confirmPassword.getText().toString())) {

                    Common.adminRef.orderByChild("memberId").equalTo(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot :snapshot.getChildren()) {
                                    AdminModel model = dataSnapshot.getValue(AdminModel.class);
                                    assert model != null;
                                    Common.adminRef.child(model.getId()).child("password").setValue(firstPassword.getText().toString());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });


                   // Common.adminRef.child(phone).child("password").setValue(firstPassword.getText().toString());

                    startActivity(new Intent(ResetPasswordActivity.this,LogIn.class));
                    finish();

                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Password Doesn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}