package com.example.spotty.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotty.R;
import com.example.spotty.service.model.User;

import java.util.List;

public class LocationFriendsAdapter extends RecyclerView.Adapter<LocationFriendsAdapter.MyViewHolder> {

    private List<User> mDataset;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout textView;

        MyViewHolder(LinearLayout v) {
            super(v);
            textView = v;
        }
    }

    public LocationFriendsAdapter(List<User> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public LocationFriendsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.spt_friend_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView name = (TextView) holder.textView.findViewById(R.id.friend_name);
        User user = mDataset.get(position);
        name.setText(user.mail);

    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}