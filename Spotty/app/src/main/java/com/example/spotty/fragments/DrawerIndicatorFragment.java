package com.example.spotty.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spotty.MainActivity;
import com.example.spotty.databinding.DrawerIndicatorFragmentBinding;

public class DrawerIndicatorFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DrawerIndicatorFragmentBinding binding = DrawerIndicatorFragmentBinding.inflate(inflater, container, false);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        View view = binding.getRoot();
        ImageButton indicator = binding.drawerIndicator;
        indicator.setOnClickListener((view1) -> {
                    try {
                        ((MainActivity) getActivity()).drawerToglle();
                    } catch (NullPointerException e) {
                        Log.d("Drawer","Couldn't toggle drawer");
                    }
                }
        );

        return view;
    }


}
