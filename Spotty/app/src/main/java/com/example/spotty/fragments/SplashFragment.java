package com.example.spotty.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.spotty.MainActivity;
import com.example.spotty.R;
import com.example.spotty.ViewModels.ConnectionViewModel;
import com.example.spotty.ViewModels.SettingsViewModel;
import com.example.spotty.databinding.SplashFragmentBinding;
import com.example.spotty.service.repository.ServerRepository;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Objects;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SplashFragment extends Fragment {
    private SettingsViewModel settingsViewModel;
    private ConnectionViewModel model;
    private Dialog dialog;
    private SplashFragmentBinding binding;


    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SplashFragmentBinding.inflate(inflater, container, false);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    private void goToMain() {
        NavController host = Navigation.findNavController(getView());
        if (host.getCurrentDestination().getId() == R.id.splash) {
            Boolean animate = settingsViewModel.getAnimate(getContext());
            int delay = animate ? 350 : 0;
            if (animate) {
                GifImageView gif = binding.splashGif;
                gif.animate().alpha(0.0f).setDuration(300);
                TextView textView = binding.splashScreenTekst;
                textView.animate().alpha(0.0f).setDuration(300);
            }
            final Handler handler = new Handler();
            handler.postDelayed(() -> {

                host.navigate(R.id.action_splashFragment_to_mainFragment);
                ((MainActivity) getActivity()).startInternetCheck();
            }, delay);
        }
    }

    private boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        return (status != ConnectionResult.SUCCESS);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsViewModel = ViewModelProviders.of(Objects.requireNonNull(this)).get(SettingsViewModel.class);
        model = ViewModelProviders.of(Objects.requireNonNull(this)).get(ConnectionViewModel.class);
        //vraag internet
        int MY_PERMISSIONS_REQUEST_INTERNET = 0;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);
        }
        ServerRepository serverRepository = ServerRepository.getInstance();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_INTERNET);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_INTERNET);
        }
        if (isGooglePlayServicesAvailable(getActivity())) {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.no_play_service))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setNegativeButton(getString(R.string.exit), (DialogInterface dialog, int which) -> getActivity().finish())
                    .show();
        }
        //Alle checks voor deze!
        checkInternet();
    }

    //in apart methode anders wordt er een windows geleaked!
    private void finish() {
        try {
            dialog.dismiss();
            Objects.requireNonNull(getActivity()).finish();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) dialog.dismiss();
    }

    private void checkInternet() {
        model.isConnected().observe(getViewLifecycleOwner(), (connected) -> {
            if (dialog == null) {
                dialog = new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.error))
                        .setMessage(getString(R.string.unable_to_reach_servers))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(getString(R.string.retry), (DialogInterface dia, int which) -> {
                            dia.cancel();
                            model.trigger();
                        })
                        .setNegativeButton(getString(R.string.exit), (DialogInterface dia, int which) -> finish())
                        .create();
            }
            if (!connected) {
                dialog.show();
            } else {
                model.isConnected().removeObservers(getViewLifecycleOwner());
                GifImageView gif = binding.splashGif;
                GifDrawable gifDrawable = (GifDrawable) gif.getDrawable();

                new Thread(() -> {
                    while (gifDrawable.isRunning()) {
                        if (!(gifDrawable.getCurrentPosition() < gifDrawable.getDuration())) {
                            gifDrawable.stop();
                        }
                    }
                    Objects.requireNonNull(getActivity()).runOnUiThread(this::goToMain);
                }).start();

            }
        });
    }
}
