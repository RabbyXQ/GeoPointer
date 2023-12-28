package com.bosonixbd.placemarker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import android.Manifest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapView;
import android.content.pm.PackageManager;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import android.location.Location;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import androidx.annotation.NonNull;
import com.google.android.gms.location.FusedLocationProviderClient;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private Button currentLocationBtn;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button currentLocationBtn = findViewById(R.id.currentLocation);
        currentLocationBtn.setOnClickListener(view -> requestCurrentLocation());

        Button saveButton = findViewById(R.id.saveCordinate);
        Button placeBtn = findViewById(R.id.placeBtn);
        placeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), PlacesActivity.class));
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        LatLng sydney = new LatLng(-34, 151);
        myMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        marker = myMap.addMarker(new MarkerOptions().position(sydney).title("Sydney"));
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        myMap.setOnMapClickListener(latLng -> {
            if (marker != null) {
                marker.setPosition(latLng);
            } else {
                marker = myMap.addMarker(new MarkerOptions().position(latLng).title("Moved Marker"));
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }
        });
    }

    private void requestCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.requestLocationUpdates(
                    LocationRequest.create(),
                    new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult != null) {
                                for (Location location : locationResult.getLocations()) {
                                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                                    MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).title("Current Location");
                                    if (marker != null) {
                                        marker.setPosition(currentLatLng);
                                    } else {
                                        marker = myMap.addMarker(markerOptions);
                                    }
                                }
                            }
                        }
                    },
                    null
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestCurrentLocation();
            } else {
                // Handle permission denied
            }
        }
    }

    // Implement onPause, onDestroy, and onLowMemory similarly to onResume
}
