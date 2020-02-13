package com.example.spotty.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotty.R;
import com.example.spotty.ViewModels.UserProfileViewModel;
import com.example.spotty.service.model.User;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {
    private UserProfileViewModel model;
    private List<User> mDataset;

    public FriendsAdapter(List<User> myDataset, UserProfileViewModel model) {
        mDataset = myDataset;
        this.model = model;
    }

    @Override
    public FriendsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView name = (TextView) holder.textView.findViewById(R.id.friend_name);
        Button button = (Button) holder.textView.findViewById(R.id.approve);
        User user = mDataset.get(position);
        name.setText(user.mail);
        switch (user.isFriend){
            case 2:{
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener((view) -> {
                    button.setEnabled(false);
                    model.approveFriendRequest(mDataset.get(position).mail);
                    button.setVisibility(View.GONE);
                });
                break;
            }
            case -1:{
                button.setVisibility(View.VISIBLE);
                button.setText((R.string.reuest_friend));
                button.setOnClickListener((view) -> {
                    button.setEnabled(false);
                    model.sendFriendRequest(mDataset.get(position).mail);
                    button.setVisibility(View.GONE);
                });
                break;
            }
            case 0:{
                button.setVisibility(View.VISIBLE);
                button.setText(("Request send"));
                button.setEnabled(false);
                break;
            }
            case 1:{
                button.setVisibility(View.GONE);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout textView;

        MyViewHolder(ConstraintLayout v) {
            super(v);
            textView = v;
        }
    }
}