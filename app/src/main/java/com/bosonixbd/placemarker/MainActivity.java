package com.bosonixbd.placemarker;

import android.content.Intent;
import android.os.Bundle;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import android.Manifest;
import android.widget.EditText;
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
    private CurrentLocation presentLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Marker marker;

    public PlacesList places;

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
        saveButton.setOnClickListener(v->showCustomDialog());
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


        requestCurrentLocation();

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
    public void showCustomDialog() {
        // Create a dialog instance
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);

        EditText editText1 = dialog.findViewById(R.id.editText1);
        EditText editText2 = dialog.findViewById(R.id.editText2);
        EditText editText3 = dialog.findViewById(R.id.editText3);
        Button dialogButton = dialog.findViewById(R.id.dialogButton);

        dialogButton.setOnClickListener(v -> {
            // Perform action when the dialog button is clicked
            String text1 = editText1.getText().toString();
            String text2 = editText2.getText().toString();
            String text3 = editText3.getText().toString();

            // Process the input values here (e.g., validate, store, etc.)

            // Dismiss the dialog
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }
    // Implement onPause, onDestroy, and onLowMemory similarly to onResume
}
