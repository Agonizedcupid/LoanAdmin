package com.example.loanadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loanadmin.Common.Common;
import com.example.loanadmin.LoanEnquiryDetails;
import com.example.loanadmin.Model.LoanModel;
import com.example.loanadmin.Model.UserDetailsModel;
import com.example.loanadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoanEnquiryAdapter extends RecyclerView.Adapter<LoanEnquiryAdapter.ViewHolder> {

    private Context context;
    private List<LoanModel> list;

    public LoanEnquiryAdapter(Context context,List<LoanModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_loan_enquiry_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull LoanEnquiryAdapter.ViewHolder holder, int position) {

        LoanModel model = list.get(position);
        String id = model.getUserId();
        holder.category.setText(model.getLoanCategory());
        //holder.userName.setText("Amount: "+model.getLoanAmount());

        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoanEnquiryDetails.class);
                intent.putExtra("id",model.getId());
                context.startActivity(intent);
            }
        });

        Common.userDetailsRef.orderByChild("userId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        UserDetailsModel m = dataSnapshot.getValue(UserDetailsModel.class);
                        holder.userName.setText(m.getUserName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private TextView category;

        private LinearLayout expand;

        public ViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userNameInSingleList);
            category = itemView.findViewById(R.id.loanCategoryInSingleList);
            expand = itemView.findViewById(R.id.expandLoanEnquiryList);
        }
    }
}
