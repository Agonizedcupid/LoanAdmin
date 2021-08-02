package com.example.loanadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.loanadmin.Adapter.LoanEnquiryAdapter;
import com.example.loanadmin.Common.Common;
import com.example.loanadmin.Model.LoanModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnquiryListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<LoanModel> list = new ArrayList<>();

    private ProgressBar progressBar;

    private RadioButton all,rejected,accepted;

    private String check = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry_list);

        initUI();

        loadData(check);
    }

    private void loadData(String check) {
        Common.loanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        LoanModel model = dataSnapshot.getValue(LoanModel.class);
                        assert model != null;

//                        if (!model.getStatus().equals("Accepted") || !model.getStatus().equals("Rejected"))
//                            list.add(model);

//                        if (model.getStatus().equals("no"))
//                            list.add(model);

                        if (check.equals("All")) {
                            list.add(model);
                        } else if (check.equals("Accepted")) {
                            if (model.getStatus().equals("Accepted")) {
                                list.add(model);
                            }
                        } else if (check.equals("Rejected")) {
                            if (model.getStatus().equals("Rejected")) {
                                list.add(model);
                            }
                        }
                    }
                    LoanEnquiryAdapter adapter = new LoanEnquiryAdapter(EnquiryListActivity.this, list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(EnquiryListActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void initUI() {

        all = findViewById(R.id.allLoanBtn);
        rejected = findViewById(R.id.rejectedBtn);
        accepted = findViewById(R.id.acceptedBtn);

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = "All";
                loadData(check);
            }
        });

        rejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = "Rejected";
                loadData(check);
            }
        });

        accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = "Accepted";
                loadData(check);
            }
        });

        progressBar = findViewById(R.id.progressbar);
        findViewById(R.id.backBtn).setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}