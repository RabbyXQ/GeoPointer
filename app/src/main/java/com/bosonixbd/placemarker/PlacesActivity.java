package com.bosonixbd.placemarker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;
public class PlacesActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        // Sample data - Replace this with your actual data source
        ArrayList<Place> places = new ArrayList<>();
        places.add(new Place("Dhaka", 23.777176, 90.399452));
        places.add(new Place("Place B", 34.0522, -118.2437));
        places.add(new Place("Place C", 51.5074, -0.1278));

        listView = findViewById(R.id.listView);

        // Create and set the custom adapter
        CustomAdapter adapter = new CustomAdapter(this, R.layout.list_item_layout, places);
        listView.setAdapter(adapter);
    }
}