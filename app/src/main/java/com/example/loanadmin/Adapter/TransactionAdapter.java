package com.example.loanadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.loanadmin.Model.TransactionModel;
import com.example.loanadmin.R;
import com.example.loanadmin.TransactionDetailsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context context;
    private List<TransactionModel> list;

    public TransactionAdapter(Context context, List<TransactionModel>list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_transaction_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TransactionAdapter.ViewHolder holder, int position) {
        TransactionModel model = list.get(position);
        holder.userName.setText("User Name: "+model.getTransactionName());
        holder.loanAmount.setText("Amount: "+model.getTransactionFine()+" "+model.getTransactionAmount());
        holder.title.setText("Title: "+model.getTitle());

        holder.expandTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TransactionDetailsActivity.class);
                intent.putExtra("id",model.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName,loanAmount,title;
        private LinearLayout expandTransaction;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userNameInTransactionList);
            loanAmount = itemView.findViewById(R.id.amountInTransactionList);
            title = itemView.findViewById(R.id.contributionTitle);

            expandTransaction = itemView.findViewById(R.id.expandTransaction);
        }
    }
}
