package com.example.spotty.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotty.R;
import com.example.spotty.service.model.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder> {

    private List<Location> mDataset;
    private NavController navController;

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;

        MyViewHolder(LinearLayout v) {
            super(v);
            linearLayout = v;

        }

    }

    public LocationAdapter(List<Location> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public LocationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.spt_friend_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView name = (TextView) holder.linearLayout.findViewById(R.id.friend_name);
        Location location = mDataset.get(position);
        name.setText(location.name);
        name.setOnClickListener((view) -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("loc-id", location.id);
                    navController.navigate(R.id.action_global_spotFragment, bundle);
                }
        );
    }

    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }
}