package com.example.loanadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.loanadmin.Common.Common;
import com.example.loanadmin.Interface.APISerrvice;
import com.example.loanadmin.Model.AdminModel;
import com.example.loanadmin.Model.LoanModel;
import com.example.loanadmin.Model.NotificationModel;
import com.example.loanadmin.Model.UserDetailsModel;
import com.example.loanadmin.Notification.Client;
import com.example.loanadmin.Notification.Data;
import com.example.loanadmin.Notification.MyResponse;
import com.example.loanadmin.Notification.Sender;
import com.example.loanadmin.Notification.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoanEnquiryDetails extends AppCompatActivity {

    private TextView userName, permanentAddress, aadharNumber, panNumber, interestRate;
    private TextView dob, acceptBtn, rejectBtn;
    private TextView gender, uploadProof, loanCategory;
    private TextView loanAmount,tenure;

    private ProgressBar progressBar;

    private ImageView proofImage;

    private String id = "";

    //Notification Part:
    APISerrvice apiSerrvice;
    private static String whatTypes = "";

    private String invitationMessage = "Your loan approved!";
    private String receiverId;
    public static boolean notify = false;

    private String notificationStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_enquiry_details);

        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
        }

        apiSerrvice = Client.getClient("https://fcm.googleapis.com/").create(APISerrvice.class);

        initUI();

        loadData();
    }

    private void loadData() {
        Common.loanRef.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        LoanModel model  = snapshot.getValue(LoanModel.class);
                        assert model != null;

                        userName.setText(model.getName());
                        dob.setText(model.getDob());
                        gender.setText(model.getGender());
                        permanentAddress.setText(model.getAddress());
                        aadharNumber.setText(model.getAadhar());
                        panNumber.setText(model.getPan());
                        uploadProof.setText(model.getUploadProof());
                        loanCategory.setText(model.getLoanCategory());
                        interestRate.setText(model.getInterestRate());
                        Glide.with(LoanEnquiryDetails.this).load(model.getUploadProofImage()).into(proofImage);


                        loanAmount.setText("Amount: "+model.getLoanAmount());
                        tenure.setText(model.getTenure());

                        final ImagePopup imagePopup = new ImagePopup(LoanEnquiryDetails.this);
                        proofImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                /** Initiate Popup view **/
                                imagePopup.initiatePopup(proofImage.getDrawable());
                                imagePopup.setHideCloseIcon(true);
                                imagePopup.viewPopup();
                            }
                        });


                        progressBar.setVisibility(View.GONE);

                        acceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notificationStatus = "Accepted";
                                acceptLoan(model);
                            }
                        });

                        rejectBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notificationStatus = "Rejected";
                                acceptLoan(model);
                            }
                        });

                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoanEnquiryDetails.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Loan accept:
    private void acceptLoan(LoanModel model) {


        receiverId = model.getUserId();

        notify = true;
        sendMessage(Common.userIdForNotification, receiverId, notificationStatus, invitationMessage,model.getId());
    }

    private void sendMessage(String userIdForNotification, String receiverId, String notificationStatus, String invitationMessage,String idForNotification) {
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
                    sendNotification(receiverId, notificationStatus, msg,idForNotification);
                }

                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(final String receiver, final String notificationStatus, final String msg,String idForNotification) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");

        Query query = tokens.orderByKey().equalTo(receiver);
        String title = "Your loan has been "+notificationStatus;

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

                    Log.d("TOKEN_RESULT",token.getToken());

                    apiSerrvice.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                            //Toast.makeText(context, "" + response.code(), Toast.LENGTH_SHORT).show();

                            if (response.isSuccessful()) {
                                //Toast.makeText(LoanEnquiryDetails.this, "Notification Sent", Toast.LENGTH_SHORT).show();
                                saveNotification(receiverId, notificationStatus, title,idForNotification);
                                //Toast.makeText(context, "Invitation sent.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoanEnquiryDetails.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                            Toast.makeText(LoanEnquiryDetails.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LoanEnquiryDetails.this, "Notification Sent", Toast.LENGTH_SHORT).show();
                Common.loanRef.child(receiverId).child("status").setValue(notificationStatus);

                startActivity(new Intent(LoanEnquiryDetails.this,EnquiryListActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(LoanEnquiryDetails.this, "Unable to send notification", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void initUI() {

        userName = findViewById(R.id.nameInLoan);
        dob = findViewById(R.id.dobInLoan);
        gender = findViewById(R.id.genderInLoan);
        permanentAddress = findViewById(R.id.permanentAddressInLoan);
        aadharNumber = findViewById(R.id.aadharNumberInLoan);
        panNumber = findViewById(R.id.panNumberInLoan);
        uploadProof = findViewById(R.id.uploadProofInLoan);
        loanCategory = findViewById(R.id.loanCategoryInLoan);
        interestRate = findViewById(R.id.interestRateInLoan);
        proofImage = findViewById(R.id.proofImage);

        loanAmount = findViewById(R.id.loanAmount);
        tenure = findViewById(R.id.tenure);

        acceptBtn = findViewById(R.id.acceptInLon);
        rejectBtn = findViewById(R.id.rejectInLon);

        progressBar = findViewById(R.id.progressbar);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }
}