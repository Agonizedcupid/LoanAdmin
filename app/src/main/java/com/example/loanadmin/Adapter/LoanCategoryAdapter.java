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

import com.example.loanadmin.AddLoanActivity;
import com.example.loanadmin.Model.LoanCategoryModel;
import com.example.loanadmin.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoanCategoryAdapter extends RecyclerView.Adapter<LoanCategoryAdapter.ViewHolder> {

    private Context context;
    private List<LoanCategoryModel> list;

    public LoanCategoryAdapter (Context context, List<LoanCategoryModel>list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_loan_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LoanCategoryAdapter.ViewHolder holder, int position) {
        LoanCategoryModel model = list.get(position);
        holder.loanName.setText(model.getTypeOfLoan());
        holder.memberPercentage.setText(model.getMemberPercentage()+" %");
        holder.nonMemberPercentage.setText(model.getNonMemberPercentage()+" %");

        holder.expandLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddLoanActivity.class);
                intent.putExtra("id",model.getId());
                intent.putExtra("check","false");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView loanName,memberPercentage,nonMemberPercentage;

        private LinearLayout expandLoan;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            loanName = itemView.findViewById(R.id.loanName);
            memberPercentage = itemView.findViewById(R.id.memberPercentage);
            nonMemberPercentage = itemView.findViewById(R.id.nonMemberPercentage);
            expandLoan = itemView.findViewById(R.id.expandLoan);
        }
    }
}
