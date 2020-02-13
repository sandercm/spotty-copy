package com.example.spotty.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.spotty.MainActivity;
import com.example.spotty.R;
import com.example.spotty.ViewModels.UserProfileViewModel;
import com.example.spotty.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private UserProfileViewModel model = new UserProfileViewModel();
    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    private void setError(Button loginButton, TextView loginError, int textId) {
        loginError.setVisibility(View.VISIBLE);
        loginError.setText(textId);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button loginButton = binding.loginButton;
        EditText emailInput = binding.usernameInput;
        EditText passwordInput = binding.passwordInput;
        TextView registerRequest = binding.registerRequest;
        TextView loginError = binding.errorText;
        loginButton.setEnabled(true);
        loginButton.setOnClickListener((view1) -> {
            loginButton.setEnabled(false);
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            if (email.equals("") || password.equals("")) {
                setError(loginButton, loginError, R.string.login_empty_fields_error);
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                setError(loginButton, loginError, R.string.login_email_formatting);
                return;
            }
            model.logIn(email, password, getContext()).observe(this, value -> {
                if (value != null) {
                    if (value) {
                        ((MainActivity) getActivity()).moveToHome();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.error_login), Toast.LENGTH_SHORT).show();
                        setError(loginButton, loginError, R.string.login_failed_error);
                    }
                }
            });
        });
        registerRequest.setOnClickListener((view1) -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }
}
