package com.example.spotty.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotty.service.model.LocationDataWeek;
import com.example.spotty.service.repository.ServerRepository;

import java.util.List;

public class SpotViewmodel extends ViewModel {
    private final ServerRepository repository = ServerRepository.getInstance();

    public LiveData<List<LocationDataWeek>> getLocationCrowdednessWeek(int id) {
        return repository.getLocationCrowdednessWeek(id);
    }
}
