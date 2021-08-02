package com.example.loanadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.loanadmin.Adapter.LoanCategoryAdapter;
import com.example.loanadmin.Common.Common;
import com.example.loanadmin.Model.LoanCategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LoanCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LoanCategoryAdapter adapter;
    private ImageView backBtn,addLoan;

    private ProgressBar progressBar;

    private List<LoanCategoryModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_category);

        initUi();

        loadData();
    }

    private void loadData() {


        Common.loanListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    list.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        LoanCategoryModel model = snapshot.getValue(LoanCategoryModel.class);
                        list.add(model);
                    }
                    adapter = new LoanCategoryAdapter(LoanCategoryActivity.this,list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoanCategoryActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUi() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager( this));

        progressBar = findViewById(R.id.progressbar);

        backBtn = findViewById(R.id.backBtn);
        addLoan = findViewById(R.id.addLoan);

        addLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoanCategoryActivity.this,AddLoanActivity.class);
                intent.putExtra("id","");
                intent.putExtra("check","true");
                startActivity(new Intent(intent));
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }
}