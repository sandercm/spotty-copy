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
import com.example.spotty.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {

    private UserProfileViewModel model = new UserProfileViewModel();
    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    private void setError(Button registerButton, TextView registerError, int textId) {
        registerError.setVisibility(View.VISIBLE);
        registerError.setText(textId);
        registerButton.setEnabled(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button registerButton = binding.registerButton;
        EditText passwordInput = binding.passwordInput;
        EditText passwordConfirmationInput = binding.passwordConfirmationInput;
        EditText emailInput = binding.emailInput;
        TextView registerError = binding.errorText;
        TextView loginRequest = binding.loginRequest;
        registerButton.setEnabled(true);
        registerButton.setOnClickListener((view1) -> {
            registerError.setVisibility(View.GONE);
            registerButton.setEnabled(false);
            String password = passwordInput.getText().toString();
            String passwordConfirmation = passwordConfirmationInput.getText().toString();
            String email = emailInput.getText().toString();
            if (password.equals("")
                    || passwordConfirmation.equals("")
                    || email.equals("")) {
                setError(registerButton, registerError, R.string.login_empty_fields_error);
                return;
            }
            if (!password.equals(passwordConfirmation)) {
                setError(registerButton, registerError, R.string.register_passwords_differ_error);
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                setError(registerButton, registerError, R.string.register_invalid_email_error);
                return;
            }
            if (password.length() < 8) {
                setError(registerButton, registerError, R.string.password_length_error);
                return;
            }
            if (!password.matches("^.*[A-Z].*$")) {
                setError(registerButton, registerError, R.string.password_needs_capital);
                return;
            }
            if (!password.matches("^.*[0-9].*$")) {
                setError(registerButton, registerError, R.string.password_needs_number);
                return;
            }
            model.register(email, password, getContext()).observe(this, value -> {
                if (value != null) {
                    if (value) {
                        ((MainActivity) getActivity()).moveToHome();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.error_register), Toast.LENGTH_SHORT).show();
                        setError(registerButton, registerError, R.string.register_failed_error);
                    }
                }
            });
        });
        loginRequest.setOnClickListener((view1) ->
                NavHostFragment.findNavController(this).navigate(R.id.action_registerFragment_to_loginFragment)
        );
    }

}
