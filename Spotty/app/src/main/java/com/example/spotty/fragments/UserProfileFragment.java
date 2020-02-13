package com.example.spotty.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.spotty.R;
import com.example.spotty.ViewModels.UserProfileViewModel;

import java.util.Objects;

public class UserProfileFragment extends Fragment {
    private UserProfileViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(Objects.requireNonNull(this)).get(UserProfileViewModel.class);
        initProfile();
    }

    private void initProfile() {
        viewModel.getUser().observe(getActivity(), (user -> {
            TextView textView = (TextView) getActivity().findViewById(R.id.name);
            textView.setText(user.mail);
        }));
    }
}