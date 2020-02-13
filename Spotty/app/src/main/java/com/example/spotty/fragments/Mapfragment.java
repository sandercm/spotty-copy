package com.example.spotty.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spotty.databinding.SpotMapFragmentBinding;

/**
 * this is just a combiner class!
 */
public class Mapfragment extends Fragment {
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SpotMapFragmentBinding binding = SpotMapFragmentBinding.inflate(inflater, container, false);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }
}
