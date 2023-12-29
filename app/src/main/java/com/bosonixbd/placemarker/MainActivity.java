package com.bosonixbd.placemarker;

import android.content.Intent;
import android.os.Bundle;
import android.app.Dialog;
import android.widget.Toast;
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
import android.widget.ListView;
import android.content.Context;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import androidx.annotation.NonNull;
import com.google.android.gms.location.FusedLocationProviderClient;
import android.content.SharedPreferences;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    protected ListView listView;
    protected CustomAdapter adapter;
    private Button currentLocationBtn;
    private double curr_x, curr_y;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Marker marker;

    public ArrayList<Place> places = new ArrayList<Place>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        retrievePlacesFromSharedPreferences();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        EditText editName = findViewById(R.id.editTextText);

        Button currentLocationBtn = findViewById(R.id.currentLocation);
        currentLocationBtn.setOnClickListener(view -> requestCurrentLocation());

        Button saveButton = findViewById(R.id.saveCordinate);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placeName = editName.getText().toString();
                if(placeName.isEmpty())
                {
                    showToast(getApplicationContext(), "Please Give a name to the marked place or your current location");
                }else
                {
                    curr_x = marker.getPosition().latitude;
                    curr_y = marker.getPosition().longitude;
                    marker.setTitle(placeName);
                    places.add(new Place(placeName, curr_x, curr_y));
                }
            }
        });
        Button placeBtn = findViewById(R.id.placeBtn);


        placeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getBaseContext(), PlacesActivity.class));
            }
        });

    }
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                                    curr_x = location.getLatitude();
                                    curr_y = location.getLongitude();
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


    @Override
    protected void onPause() {
        super.onPause();
        // Save 'places' list to SharedPreferences when the app pauses
        savePlacesToSharedPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Save 'places' list to SharedPreferences when the app gets destroyed
        savePlacesToSharedPreferences();
    }

    private void savePlacesToSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("YourPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Convert 'places' list to JSON string
        Gson gson = new Gson();
        String placesJson = gson.toJson(places);

        // Save the JSON string to SharedPreferences
        editor.putString("placesList", placesJson);
        editor.apply();
    }

    private void retrievePlacesFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("YourPrefs", MODE_PRIVATE);
        String placesJson = prefs.getString("placesList", null);

        if (placesJson != null) {
            // Convert JSON string back to 'places' list
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Place>>(){}.getType();
            places = gson.fromJson(placesJson, type);
        } else {
            // If 'placesList' key is not found or null, create a new list
            places = new ArrayList<Place>();
        }
    }


    // Implement onPause, onDestroy, and onLowMemory similarly to onResume
}
