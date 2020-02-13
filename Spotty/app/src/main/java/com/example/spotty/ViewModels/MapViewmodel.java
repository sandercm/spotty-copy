package com.example.spotty.ViewModels;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotty.service.model.Location;
import com.example.spotty.service.model.LocationDataDay;
import com.example.spotty.service.model.SpottyPicture;
import com.example.spotty.service.repository.ServerRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class MapViewmodel extends ViewModel {
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    private static final int LOCATION_UPDATE_MIN_TIME = 5000;
    private final ServerRepository serverRepository = ServerRepository.getInstance();
    private HashMap<String, Location> markerLocation = new HashMap<>();
    private LocationManager mLocationManager;
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            if (location != null) {
                mLocationManager.removeUpdates(mLocationListener);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public HashMap<String, Location> getMarkerLocation() {
        return markerLocation;
    }

    public LiveData<List<Location>> getLocationList() {
        return serverRepository.getLocationList();
    }

    public LiveData<Location> getLocationInfo(int id) {
        return serverRepository.getLocationInfo(id);
    }

    public List<Location> getFilteredLocationList(String searchStrings) {
        return serverRepository.getFilteredLocationList(searchStrings);
    }

    public LiveData<SpottyPicture> getPictureById(int id, Resources resources) {
        return serverRepository.getLocationPicture(id, resources);
    }

    public LiveData<LocationDataDay> getDayCrowdedness(int id) {
        return serverRepository.getLocationCrowdednessDay(id);
    }

    public LatLng getCurrentLocation(Context context) {
        android.location.Location location = null;
        boolean gps = false;
        boolean network = false;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            network = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        if (gps) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        //when no location is found with gps, try with network
        if (network && location == null) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }
        //default to gent
        return location == null ? new LatLng(51.026, 3.713) : new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void setmLocationManager(LocationManager mLocationManager) {
        this.mLocationManager = mLocationManager;
    }

    public void triggerLocationListUpdate() {
        serverRepository.triggerServerReachableUpdate();
    }
}
