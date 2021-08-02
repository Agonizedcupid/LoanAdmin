package com.example.loanadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loanadmin.Common.Common;
import com.example.loanadmin.Model.LoanCategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.UUID;

public class AddLoanActivity extends AppCompatActivity {

    private EditText typeOfLoan,rateOfInterest,memberRate,nonMemberRate;
    private TextView timeFrom,timeTo,submitLoan,updateLoan;

    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    int day, month, year;

    private String id = "",check = "";

    String date1 = "",date2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_loan);

        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
            check = getIntent().getStringExtra("check");
        }

        initUI();

        if (check.equals("false")) {
            loadData();
            submitLoan.setVisibility(View.GONE);
            updateLoan.setVisibility(View.VISIBLE);
        } else {
            submitLoan.setVisibility(View.VISIBLE);
            updateLoan.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        Common.loanListRef.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        LoanCategoryModel model = snapshot.getValue(LoanCategoryModel.class);

                        typeOfLoan.setText(model.getTypeOfLoan(), TextView.BufferType.EDITABLE);
                        rateOfInterest.setText(model.getInterest(), TextView.BufferType.EDITABLE);
                        memberRate.setText(model.getMemberPercentage(), TextView.BufferType.EDITABLE);
                        nonMemberRate.setText(model.getNonMemberPercentage(), TextView.BufferType.EDITABLE);
                        timeFrom.setText(model.getTimeStart());
                        timeTo.setText(model.getTimeEnd());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void initUI() {
        typeOfLoan = findViewById(R.id.typeOfLoan);
        rateOfInterest = findViewById(R.id.rateOfInterest);
        memberRate = findViewById(R.id.memberRate);
        nonMemberRate = findViewById(R.id.nonMemberRate);
        timeFrom = findViewById(R.id.timeFrom);
        timeTo = findViewById(R.id.timeTo);

        submitLoan = findViewById(R.id.submitLoan);
        updateLoan = findViewById(R.id.updateLoan);

        timeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStartTime();
            }
        });

        timeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEndTime();
            }
        });

        submitLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLoan();
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        updateLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.loanListRef.child(id).child("interest").setValue(rateOfInterest.getText().toString());
                Common.loanListRef.child(id).child("memberPercentage").setValue(memberRate.getText().toString());
                Common.loanListRef.child(id).child("nonMemberPercentage").setValue(nonMemberRate.getText().toString());
                Common.loanListRef.child(id).child("timeEnd").setValue(timeTo.getText().toString());
                Common.loanListRef.child(id).child("timeStart").setValue(timeFrom.getText().toString());
                Common.loanListRef.child(id).child("typeOfLoan").setValue(typeOfLoan.getText().toString());

                startActivity(new Intent(AddLoanActivity.this,LoanCategoryActivity.class));
                finish();
            }
        });
    }

    private void selectEndTime() {

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                int j = i1 + 1;

                date1 = i + " - " + j + " - " + i2;
                timeTo.setText(date1);
            }
        }, day, month, year);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();

    }

    private void selectStartTime() {

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                int j = i1 + 1;

                date2 = i + " - " + j + " - " + i2;
                timeFrom.setText(date2);
            }
        }, day, month, year);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();

    }

    private void createLoan() {
        String id = UUID.randomUUID().toString();

        LoanCategoryModel model = new LoanCategoryModel(
                id,
                typeOfLoan.getText().toString(),
                rateOfInterest.getText().toString(),
                timeFrom.getText().toString(),
                timeTo.getText().toString(),
                memberRate.getText().toString(),
                nonMemberRate.getText().toString()
        );

        Common.loanListRef.child(id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AddLoanActivity.this, "Created", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddLoanActivity.this,LoanCategoryActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(AddLoanActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}