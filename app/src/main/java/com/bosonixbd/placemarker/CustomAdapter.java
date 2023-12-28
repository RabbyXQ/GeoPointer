package com.bosonixbd.placemarker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import androidx.annotation.Nullable;
import android.content.Intent;
import android.net.Uri;
public class CustomAdapter extends ArrayAdapter<Place> {

    private Context mContext;
    private int mResource;

    public CustomAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Place> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            listItemView = inflater.inflate(mResource, parent, false);
        }

        // Get the Place object at the current position
        Place currentPlace = getItem(position);

        // Find views within the list item layout
        TextView placeNameTextView = listItemView.findViewById(R.id.placeNameTextView);
        TextView latitudeTextView = listItemView.findViewById(R.id.latitudeTextView);
        TextView longitudeTextView = listItemView.findViewById(R.id.longitudeTextView);
        Button gotoMapButton = listItemView.findViewById(R.id.gotoMapButton);

        // Set the data to views
        if (currentPlace != null) {
            placeNameTextView.setText(currentPlace.getPlaceName());
            latitudeTextView.setText("Latitude: " + currentPlace.getLatitude());
            longitudeTextView.setText("Longitude: " + currentPlace.getLongitude());

            // Set click listener for "Go to Map" button
            gotoMapButton.setOnClickListener(v -> {
                // Handle button click event, navigate to the map or perform relevant action
                // based on the place's coordinates

                // Get current location (assuming you have the current location coordinates)
//                double currentLat = currentPlace.yourLocation().getLatitude();
//                double currentLon = currentPlace.yourLocation().getLongitude();

                double destLat = currentPlace.getLatitude();
                double destLon = currentPlace.getLongitude();

                // Start an intent to open a map using the coordinates
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destLat + "," + destLon + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                // Check if Google Maps app is available
                if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
                    // If Google Maps app is available, start the navigation
                    mContext.startActivity(mapIntent);
                } else {
                    // If Google Maps app is not available, handle the situation
                    // You can prompt the user to install Google Maps or use a different approach
                    // For instance, opening a browser with Google Maps web version.
                }
            });
        }

        return listItemView;
    }
}