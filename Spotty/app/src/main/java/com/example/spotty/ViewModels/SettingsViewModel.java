package com.example.spotty.ViewModels;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.spotty.service.repository.LocalPreferencesRepository;
import com.example.spotty.service.repository.ServerRepository;

public class SettingsViewModel extends ViewModel {

    private static final String PREF_SETTINGS = LocalPreferencesRepository.PREF_SETTINGS;
    private static final String animateString = "animate";
    private static final String syncString = "sync";
    private static final LocalPreferencesRepository localPrefRepo = LocalPreferencesRepository.getInstance(ServerRepository.getInstance());

    public void setAnimate(Context context, Boolean animate) {
        localPrefRepo.setAnimate(context, animate);
    }

    public Boolean getAnimate(Context context) {
        return localPrefRepo.getAnimate(context);
    }

    public void setSync(Context context, Boolean sync) {
        localPrefRepo.setSync(context, sync);
    }

    public Boolean getSync(Context context) {
        return localPrefRepo.getSync(context);
    }

    public void setSavePassword(Context context, Boolean o) {
        localPrefRepo.setUserPasswordSaved(context, o);
    }

    public boolean getSavePassword(Context context) {
        return localPrefRepo.getUserPasswordSaved(context);
    }
}
