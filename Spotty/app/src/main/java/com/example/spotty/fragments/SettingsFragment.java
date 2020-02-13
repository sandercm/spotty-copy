package com.example.spotty.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.spotty.R;
import com.example.spotty.ViewModels.SettingsViewModel;
import com.example.spotty.databinding.SettingsBinding;

import java.util.Objects;

public class SettingsFragment extends Fragment {
    private SettingsViewModel model;
    private SettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = SettingsBinding.inflate(inflater, container, false);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        return  binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = ViewModelProviders.of(Objects.requireNonNull(this)).get(SettingsViewModel.class);

        //init switches and their listeners
        initSwitch();

    }

    private void initSwitch() {
        {
            Switch animate = binding.animate;
            animate.setChecked(model.getAnimate(getContext()));
            animate.setOnCheckedChangeListener((button, bool) -> model.setAnimate(getContext(), bool));
        }
        {
            Switch sync = binding.sync;
            sync.setChecked(model.getSync(getContext()));
            sync.setOnCheckedChangeListener((button, bool) -> model.setSync(getContext(), bool));
        }
        {
            Switch save_password = binding.savePassword;
            save_password.setChecked(model.getSavePassword(getContext()));
            save_password.setOnCheckedChangeListener(((buttonView, isChecked) -> model.setSavePassword(getContext(), isChecked)));
        }
    }
}