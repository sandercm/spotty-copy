package com.example.spotty.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotty.service.repository.ServerRepository;

public class ConnectionViewModel extends ViewModel {

    private final ServerRepository repository = ServerRepository.getInstance();

    public LiveData<Boolean> isConnected() {
        return repository.serverReachableLive();
    }

    public void trigger() {
        repository.triggerServerReachableUpdate();
    }
}
