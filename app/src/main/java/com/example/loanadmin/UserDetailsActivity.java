package com.example.loanadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.loanadmin.Common.Common;
import com.example.loanadmin.Model.UserDetailsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class UserDetailsActivity extends AppCompatActivity {

    private TextView userName, permanentAddress, currentAddress, AadharNumber, panNumber, bankName, branch, IFSCCOde, bacnkAccountNumber;
    private TextView dob,gender,memberId;

    private String id = "";

    private ImageView firstImage,secondImage,thirdImage;
    ImagePopup imagePopupOne, imagePopupTwo, imagePopupThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        imagePopupOne = new ImagePopup(UserDetailsActivity.this);
        imagePopupTwo = new ImagePopup(UserDetailsActivity.this);
        imagePopupThree = new ImagePopup(UserDetailsActivity.this);

        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
        }

        initUI();

        loadData();

    }

    private void loadData() {
        Common.userDetailsRef.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        UserDetailsModel model = snapshot.getValue(UserDetailsModel.class);
                        assert model != null;
                        userName.setText(model.getUserName());
                        permanentAddress.setText(model.getPermanentAddress());
                        currentAddress.setText(model.getCurrentAddress());
                        AadharNumber.setText(model.getAadharNumber());
                        panNumber.setText(model.getPanNumber());
                        bankName.setText(model.getBankName());
                        branch.setText(model.getBranch());
                        IFSCCOde.setText(model.getCode());
                        bacnkAccountNumber.setText(model.getBankAccount());
                        dob.setText(model.getDob());
                        gender.setText(model.getGender());
                        memberId.setText(model.getMemberId());

                        imagePopupOne.setFullScreen(true);
                        imagePopupTwo.setFullScreen(true);
                        imagePopupThree.setFullScreen(true);

                        if (model.getList().size() >=3) {
                            Glide.with(UserDetailsActivity.this).load(model.getList().get(0).getImageUrl())
                                    .into(firstImage);

                            Glide.with(UserDetailsActivity.this).load(model.getList().get(1).getImageUrl())
                                    .into(secondImage);

                            Glide.with(UserDetailsActivity.this).load(model.getList().get(2).getImageUrl())
                                    .into(thirdImage);
                        } else if (model.getList().size() == 2) {
                            Glide.with(UserDetailsActivity.this).load(model.getList().get(0).getImageUrl())
                                    .into(firstImage);

                            Glide.with(UserDetailsActivity.this).load(model.getList().get(1).getImageUrl())
                                    .into(secondImage);
                        } else if (model.getList().size() == 1) {
                            Glide.with(UserDetailsActivity.this).load(model.getList().get(0).getImageUrl())
                                    .into(firstImage);
                        } else if (model.getList().size() < 1) {
                            Toast.makeText(UserDetailsActivity.this, "No Image Provided!", Toast.LENGTH_SHORT).show();
                        }

                        firstImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                imagePopupOne.initiatePopup(firstImage.getDrawable());
                                imagePopupOne.viewPopup();
                            }
                        });


                        secondImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                imagePopupTwo.initiatePopup(secondImage.getDrawable());
                                imagePopupTwo.viewPopup();
                            }
                        });

                        thirdImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                imagePopupThree.initiatePopup(thirdImage.getDrawable());
                                imagePopupThree.viewPopup();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void initUI() {
        userName = findViewById(R.id.nameInUserDtls);
        permanentAddress = findViewById(R.id.permanentAddressInUserDtls);
        currentAddress = findViewById(R.id.currentAddressUserDtls);
        AadharNumber = findViewById(R.id.aadharNumberInUserDtls);
        panNumber = findViewById(R.id.panNumberInUserDtls);
        bankName = findViewById(R.id.bankNameInUserDtls);
        branch = findViewById(R.id.branchNameInUserDtls);
        IFSCCOde = findViewById(R.id.ifscCodeInUserDtls);
        bacnkAccountNumber = findViewById(R.id.bankAccountInUserDtls);
        memberId = findViewById(R.id.memberId);

        dob = findViewById(R.id.dobInUserDtls);
        gender = findViewById(R.id.genderInUserDtls);

        firstImage = findViewById(R.id.firstImage);
        secondImage = findViewById(R.id.secondImage);
        thirdImage = findViewById(R.id.thirdImage);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}