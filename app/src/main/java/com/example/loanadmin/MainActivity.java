package com.example.loanadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.loanadmin.Common.Common;
import com.example.loanadmin.Notification.Token;
import com.facebook.login.Login;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.loanadmin.Common.Common.DEFAULT;

public class MainActivity extends AppCompatActivity {

    private LinearLayout enquiryList,userList,addTransaction,loanCategory,logOut;
    public static String User_token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        retrieveToken();
    }

    private void retrieveToken() {

        SharedPreferences sharedPreference = getSharedPreferences("TOKEN_SAVING", Context.MODE_PRIVATE);

        User_token = sharedPreference.getString("FCM_TOKEN", DEFAULT);
        updateToken(User_token);
    }

    private void updateToken(String token) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens_Admin");

        Token token1 = new Token(token);

        if (Common.userIdForNotification != null) {
            databaseReference.child(Common.userIdForNotification).setValue(token1);
        }


    }

    private void initUI() {
        enquiryList = findViewById(R.id.enquiryList);
        userList = findViewById(R.id.userList);
        addTransaction = findViewById(R.id.addTransaction);
        loanCategory = findViewById(R.id.loanCategory);
        logOut = findViewById(R.id.logOutLin);

        enquiryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,EnquiryListActivity.class));
            }
        });

        userList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UserListActivity.class));
            }
        });

        addTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TransactionsActivity.class));
            }
        });

        loanCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoanCategoryActivity.class));
            }
        });

        findViewById(R.id.bankGateway).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,BankGatewayActivity.class));
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                );

                finish();
            }
        });

        findViewById(R.id.notificationIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,NotificationActivity.class));
            }
        });
    }
}