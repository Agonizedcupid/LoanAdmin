package com.example.loanadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loanadmin.Adapter.UserListAdapter;
import com.example.loanadmin.Adapter.UserListAdapterInTransaction;
import com.example.loanadmin.Common.Common;
import com.example.loanadmin.Interface.APISerrvice;
import com.example.loanadmin.Model.AdminModel;
import com.example.loanadmin.Model.LoanModel;
import com.example.loanadmin.Model.NotificationModel;
import com.example.loanadmin.Model.TransactionModel;
import com.example.loanadmin.Model.UserDetailsModel;
import com.example.loanadmin.Notification.Client;
import com.example.loanadmin.Notification.Data;
import com.example.loanadmin.Notification.MyResponse;
import com.example.loanadmin.Notification.Sender;
import com.example.loanadmin.Notification.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsActivity extends AppCompatActivity {

    private ImageView backBtn;

    private EditText transactionTenure, transactionTitle;
    private TextView submitTransaction, transactionRecordDate, transactionMonth;
    public static TextView transactionInterest;
    public static EditText transactionAmount;
    private ImageView transactionList;

    public static TextView transactionName;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private FirebaseAuth userAuth;
    private String userId = "";

    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    int day, month, year;

    String date = "";

    private View bottomSheet;
    public static BottomSheetBehavior bottomSheetBehavior;

    private RecyclerView recyclerView;
    // private List<UserDetailsModel> list = new ArrayList<>();
    private List<LoanModel> list = new ArrayList<>();

    private RadioButton memberBtn, nonMemberBtn;
    private String selectedMember = "";
    public static String customerId = "";

    private ProgressBar progressBar;

    //Notification Part:
    APISerrvice apiSerrvice;
    private static String whatTypes = "";

    private String invitationMessage = "Your loan approved!";
    private String receiverId;
    public static boolean notify = false;

    private String notificationStatus = "";

    private String[] fineArray = {"Fine", "Without Fine", "Dr.", "Cr."};
    private Spinner fineSpinner;
    private String selectedFine = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        apiSerrvice = Client.getClient("https://fcm.googleapis.com/").create(APISerrvice.class);

        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        initUI();

        loadData();
    }

    private void loadData() {
        Common.loanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        //UserDetailsModel model = dataSnapshot.getValue(UserDetailsModel.class);
                        LoanModel model = dataSnapshot.getValue(LoanModel.class);
                        list.add(model);
                        //if (model.getStatus().equals("Accepted"))

                    }

                    UserListAdapterInTransaction adapter = new UserListAdapterInTransaction(TransactionsActivity.this, list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    //progressBar.setVisibility(View.GONE);

                } else {
                    //progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(TransactionsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


//        Common.userDetailsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    list.clear();
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        UserDetailsModel model = dataSnapshot.getValue(UserDetailsModel.class);
//                        list.add(model);
//                    }
//
//                    UserListAdapterInTransaction adapter = new UserListAdapterInTransaction(TransactionsActivity.this, list);
//                    recyclerView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//                    //progressBar.setVisibility(View.GONE);
//
//                } else {
//                    //progressBar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//                //progressBar.setVisibility(View.GONE);
//                Toast.makeText(TransactionsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void initUI() {

        transactionTitle = findViewById(R.id.transactionTitle);

        transactionList = findViewById(R.id.transactionList);
        transactionList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionsActivity.this, TransactionListActivity.class));
            }
        });

        fineSpinner = findViewById(R.id.fineSpinner);
        ArrayAdapter<String> memberAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fineArray);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        fineSpinner.setAdapter(memberAdapter);

        fineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFine = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        transactionInterest = findViewById(R.id.transactionInterestRate);
        transactionTenure = findViewById(R.id.transactionTenure);

        memberBtn = findViewById(R.id.memberRadioBtn);
        nonMemberBtn = findViewById(R.id.nonMemberRadioBtn);
        memberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberBtn.setChecked(true);
                nonMemberBtn.setChecked(false);
                selectedMember = "Member";
            }
        });

        nonMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberBtn.setChecked(false);
                nonMemberBtn.setChecked(true);
                selectedMember = "Non - Member";
            }
        });

        bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        recyclerView = findViewById(R.id.recyclerViewInBottomSheet);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transactionName = findViewById(R.id.transactionName);
        transactionRecordDate = findViewById(R.id.transactionRecordDate);
        transactionAmount = findViewById(R.id.transactionAmount);
        submitTransaction = findViewById(R.id.submitTransaction);
        transactionMonth = findViewById(R.id.transactionMonth);

        transactionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        transactionMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMonth();
            }
        });

        submitTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTransaction();
            }
        });

        transactionRecordDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRecordDate();
            }
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

    }

    private void selectRecordDate() {
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                int j = i1 + 1;

                date = i + " - " + j + " - " + i2;
                transactionRecordDate.setText(date);
            }
        }, day, month, year);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    private void selectMonth() {
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                int j = i1 + 1;

                date = i + " - " + j + " - " + i2;
                transactionMonth.setText(date);
            }
        }, day, month, year);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    private void createTransaction() {
        String id = UUID.randomUUID().toString();
//        if (userAuth.getCurrentUser() == null) {
//            userId = sharedPreferences.getString("phone", Common.DEFAULT);
//        } else {
//            userId = Common.userId;
//        }

        TransactionModel model = new TransactionModel(
                id,
                customerId,
                transactionName.getText().toString(),
                transactionMonth.getText().toString(),
                transactionRecordDate.getText().toString(),
                selectedFine,
                transactionAmount.getText().toString(),
                "",
                transactionInterest.getText().toString(),
                selectedMember,
                transactionTitle.getText().toString()

        );

        Common.transactionRef.child(id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                notificationStatus = "Created";
                TransactionForNotification(Common.userIdForNotification, customerId, notificationStatus, invitationMessage, id);

                Toast.makeText(TransactionsActivity.this, "Submitted", Toast.LENGTH_SHORT).show();
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TransactionsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void TransactionForNotification(String userIdForNotification, String receiverIds, String notificationStatus, String invitationMessage, String id) {
        receiverId = receiverIds;
        notify = true;
        sendMessage(Common.userIdForNotification, receiverId, notificationStatus, invitationMessage, id);
    }

    private void sendMessage(String userIdForNotification, String receiverId, String notificationStatus, String invitationMessage, String idForNotification) {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", userIdForNotification);
        hashMap.put("receiver", receiverId);
        hashMap.put("message", invitationMessage);

        final String msg = invitationMessage;

        Common.adminRef.child(Common.userIdForNotification).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AdminModel users = dataSnapshot.getValue(AdminModel.class);

                if (notify) {
                    //assert users != null;
                    sendNotification(receiverId, notificationStatus, msg, idForNotification);
                }

                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendNotification(final String receiver, final String notificationStatus, final String msg, String idForNotification) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");

        Query query = tokens.orderByKey().equalTo(receiver);
        String title = "Your transaction has been created";

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);

                    //Here data will be change

                    Data data = new Data(
                            Common.userIdForNotification,
                            R.mipmap.ic_launcher,
                            "Please check your notification tab for more details!",
                            title,
                            receiverId,
                            whatTypes,
                            ""
                    );

                    Sender sender = new Sender(data, token.getToken());

                    Log.d("TOKEN_RESULT", token.getToken());

                    apiSerrvice.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                            //Toast.makeText(context, "" + response.code(), Toast.LENGTH_SHORT).show();

                            if (response.isSuccessful()) {
                                //Toast.makeText(LoanEnquiryDetails.this, "Notification Sent", Toast.LENGTH_SHORT).show();
                                saveNotification(receiverId, notificationStatus, title, idForNotification);
                                //Toast.makeText(context, "Invitation sent.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TransactionsActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                            Toast.makeText(TransactionsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("TEST_RESULT", t.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveNotification(String receiverId, String notificationStatus, String title, String idForNotification) {

        String id = UUID.randomUUID().toString();
        NotificationModel model = new NotificationModel(
                id,
                idForNotification,
                Common.getCurrentDate(),
                Common.getCurrentTime(),
                receiverId,
                "",
                title,
                notificationStatus
        );

        Common.notificationRef.child(id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(TransactionsActivity.this, "Notification Sent", Toast.LENGTH_SHORT).show();
                Common.loanRef.child(receiverId).child("status").setValue("Action");

                startActivity(new Intent(TransactionsActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(TransactionsActivity.this, "Unable to send notification", Toast.LENGTH_SHORT).show();
            }
        });

    }

}