package com.example.spotty.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.spotty.R;
import com.example.spotty.ViewModels.MapViewmodel;
import com.example.spotty.service.model.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;

public class SpottyMapFragment extends com.google.android.gms.maps.SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private MapViewmodel model;

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null) {
            Log.d("MAP", "onMapReady: is called");
            mMap = googleMap;
            model.setmLocationManager((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE));
            setLocation();
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            NavController host = Navigation.findNavController(getView());
            mMap.setOnInfoWindowClickListener(marker -> {
                Bundle bundle = new Bundle();
                bundle.putInt("loc-id", Objects.requireNonNull(model.getMarkerLocation().get(marker.getId())).id);
                host.navigate(R.id.action_spottyMapFragment_to_spotFragment, bundle);
            });
            LiveData<List<Location>> liveList = model.getLocationList();
            liveList.observe(this, locations -> {
                if (locations != null) {
                    for (Location loc : locations) {
                        LatLng location = new LatLng(Double.parseDouble(loc.latitude), Double.parseDouble(loc.longitude));
                        Marker marker = mMap.addMarker(new MarkerOptions().icon(bitmapDescriptorFromVector(getContext(), R.drawable.marker)).position(location).title(loc.name).snippet(getString(R.string.crowdedness_title)));
                        model.getDayCrowdedness(loc.id).observe(this, crowdedness -> {
                            Resources res = getResources();
                            int people = crowdedness.people;
                            marker.setSnippet(res.getQuantityString(R.plurals.graph_string, people, people));
                        });
                        model.getMarkerLocation().put(marker.getId(), loc);
                    }
                }
            });
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d("MAP", "onMapReady: enters the if");
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
            }
        }
    }

    /**
     * Move the camera with to the new gps coordinates
     */
    private void setLocation() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(model.getCurrentLocation(getContext()), 14));
    }


    /**
     * We need to convert the vector to a bitmap ourselves, since a custom marker only takes bitmaps
     * @param context context
     * @param vectorResId location of drawable in the res folder
     * @return BitmapDescriptor
     */
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        vectorDrawable.draw(new Canvas(bitmap));
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMapAsync(this);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        model = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(MapViewmodel.class);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), getResources().getString(R.string.move_to_curr_loc), Toast.LENGTH_SHORT).show();
        setLocation();
        return false;
    }
}
