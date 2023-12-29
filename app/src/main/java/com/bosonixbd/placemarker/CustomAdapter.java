package com.bosonixbd.placemarker;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.widget.Toast;
public class CustomAdapter extends ArrayAdapter<Place> {

    private Context mContext;
    private int mResource;
    private ArrayList<Place> placesList;
    private SharedPreferences sharedPreferences;

    public CustomAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Place> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.placesList = objects;
        this.sharedPreferences = context.getSharedPreferences("YourPrefs", Context.MODE_PRIVATE);
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
        Button deleteBtn = listItemView.findViewById(R.id.deleteItem);
        Button shareBtn = listItemView.findViewById(R.id.share);
        shareBtn.setOnClickListener(v -> {
            double destLat = currentPlace.getLatitude();
            double destLon = currentPlace.getLongitude();

            // Create a map link using the coordinates
            String mapLink = "https://www.google.com/maps/place/" + destLat + "," + destLon;

            // Share the map link
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Map Link");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mapLink);
            mContext.startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
        TextView placeNameTextView = listItemView.findViewById(R.id.placeNameTextView);
        TextView latitudeTextView = listItemView.findViewById(R.id.latitudeTextView);
        TextView longitudeTextView = listItemView.findViewById(R.id.longitudeTextView);
        Button gotoMapButton = listItemView.findViewById(R.id.gotoMapButton);

        // Set the data to views
        if (currentPlace != null) {
            placeNameTextView.setText(currentPlace.getPlaceName());
            latitudeTextView.setText("Latitude: " + currentPlace.getLatitude());
            longitudeTextView.setText("Longitude: " + currentPlace.getLongitude());

            // Set click listener for "Delete" button
            deleteBtn.setOnClickListener(v -> {
                // Remove the item from the list
                removeItem(position);
            });

            // Set click listener for "Go to Map" button
            gotoMapButton.setOnClickListener(v -> {
                // Handle button click event, navigate to the map or perform relevant action
                // based on the place's coordinates

                // Get coordinates of the destination place
                double destLat = currentPlace.getLatitude();
                double destLon = currentPlace.getLongitude();

                // Start an intent to open a map using the coordinates
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destLat + "," + destLon + "&mode=d");

                // Copy the URI as text to the clipboard
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Google Maps URI", gmmIntentUri.toString());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(mContext, "Map link copied", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return listItemView;
    }

    // Method to remove an item from the list
    private void removeItem(int position) {
        if (position >= 0 && position < placesList.size()) {
            Place removedPlace = placesList.remove(position);
            notifyDataSetChanged(); // Notify adapter that data set has changed

            // Update SharedPreferences after removing the item
            updateSharedPreferences(removedPlace);
        }
    }

    // Method to update SharedPreferences after removing an item
    private void updateSharedPreferences(Place removedPlace) {
        // Retrieve the stored places from SharedPreferences
        String storedPlacesJson = sharedPreferences.getString("placesList", "");
        ArrayList<Place> storedPlaces = new ArrayList<>();
        if (!storedPlacesJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Place>>() {}.getType();
            storedPlaces = gson.fromJson(storedPlacesJson, type);
        }

        // Find and remove the deleted place from the stored places list
        int index = -1;
        for (int i = 0; i < storedPlaces.size(); i++) {
            Place place = storedPlaces.get(i);
            if (place.getPlaceName().equals(removedPlace.getPlaceName())
                    && place.getLatitude() == removedPlace.getLatitude()
                    && place.getLongitude() == removedPlace.getLongitude()) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            storedPlaces.remove(index);
            // Save the updated list back to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String updatedPlacesJson = gson.toJson(storedPlaces);
            editor.putString("placesList", updatedPlacesJson);
            editor.apply();
        }
    }
}
