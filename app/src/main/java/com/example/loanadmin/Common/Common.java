package com.example.loanadmin.Common;

import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static View root;
    public static DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User");
    public static DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference().child("Transactions");
    public static DatabaseReference loanRef = FirebaseDatabase.getInstance().getReference().child("ApplyForLoan");
    public static DatabaseReference userDetailsRef = FirebaseDatabase.getInstance().getReference().child("UserDetails");
    public static DatabaseReference loanListRef = FirebaseDatabase.getInstance().getReference().child("LoanList");
    public static DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("Admin");
    public static DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");
    public static DatabaseReference adminNotificationRef = FirebaseDatabase.getInstance().getReference().child("Admin_Notification");
    public static FirebaseAuth userAuth = FirebaseAuth.getInstance();
    public static String userId = "";

    public static String userIdForNotification = "";
    public static String pass = "";

    public static final String DEFAULT = "N/A";

    public static String userId () {
        if (userAuth.getCurrentUser() != null) {
            userId = userAuth.getCurrentUser().getUid();
        } else {
            userId = "";
        }

        return userId;
    }

    public static  String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static  String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String URL = "https://corporate.axisbank.co.in/wps/portal/cBanking/axiscbanking/AxisCorporateLogin/!ut/p/a1/04_Sj9CPykssy0xPLMnMz0vMAfGjzOKNAzxMjIwNjLwszI0cDRw9PU3dw3xcjP19TIAKIoEKDHAARwNC-sP1o_AqMTWFKsBjRUFuhEGmo6IiACFl0ps!/dl5/d5/L2dBISEvZ0FBIS9nQSEh/?_ga=2.71037716.525550674.1626328441-502139746.1626328441";

}
