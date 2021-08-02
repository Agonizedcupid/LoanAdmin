package com.example.loanadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loanadmin.Model.UserDetailsModel;
import com.example.loanadmin.R;
import com.example.loanadmin.UserDetailsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private Context context;
    private List<UserDetailsModel> list;

    public UserListAdapter(Context context,List<UserDetailsModel> list) {
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
    public void onBindViewHolder(@NonNull @NotNull UserListAdapter.ViewHolder holder, int position) {

        UserDetailsModel model = list.get(position);
        holder.userName.setText(model.getUserName());

        holder.expandList.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserDetailsActivity.class);
            intent.putExtra("id",model.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private LinearLayout expandList;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userNameInUserDetails);
            expandList = itemView.findViewById(R.id.expandUserList);
        }
    }
}
