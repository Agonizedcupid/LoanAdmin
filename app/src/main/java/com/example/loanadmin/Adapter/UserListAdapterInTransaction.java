package com.example.loanadmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loanadmin.Common.Common;
import com.example.loanadmin.Model.LoanModel;
import com.example.loanadmin.Model.UserDetailsModel;
import com.example.loanadmin.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.example.loanadmin.TransactionsActivity.bottomSheetBehavior;
import static com.example.loanadmin.TransactionsActivity.customerId;
import static com.example.loanadmin.TransactionsActivity.transactionAmount;
import static com.example.loanadmin.TransactionsActivity.transactionInterest;
import static com.example.loanadmin.TransactionsActivity.transactionName;

public class UserListAdapterInTransaction extends RecyclerView.Adapter<UserListAdapterInTransaction.ViewHolder> {

    private Context context;
    //private List<UserDetailsModel> list;
    private List<LoanModel> list;

    public UserListAdapterInTransaction(Context context,List<LoanModel> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_user_list,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserListAdapterInTransaction.ViewHolder holder, int position) {
       // UserDetailsModel model = list.get(position);
        LoanModel model = list.get(position);
        String id = model.getUserId();
        //holder.userName.setText(model.getName());

        putName(id);

        Common.userDetailsRef.orderByChild("userId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        UserDetailsModel m = dataSnapshot.getValue(UserDetailsModel.class);
                        assert m != null;
                        holder.userName.setText(m.getUserName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        holder.expand.setOnClickListener(v -> {

            Common.userDetailsRef.orderByChild("userId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                            UserDetailsModel m = dataSnapshot.getValue(UserDetailsModel.class);
                            assert m != null;
                            transactionName.setText(m.getUserName());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

            transactionAmount.setText(model.getLoanAmount(), TextView.BufferType.EDITABLE);
            customerId = model.getUserId();
            transactionInterest.setText(model.getInterestRate());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });


    }

    private void putName(String id) {
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private LinearLayout expand;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userNameInUserDetails);
            expand = itemView.findViewById(R.id.expandUserList);
        }
    }
}
