package com.bosonixbd.placemarker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;
import android.text.Editable;
import android.text.TextWatcher;
public class PlacesActivity extends MainActivity {

    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        searchBar = findViewById(R.id.search);
        listView = findViewById(R.id.listView);

        // Assuming 'places' is the ArrayList you want to filter
        Collections.reverse(places);

        adapter = new CustomAdapter(this, R.layout.list_item_layout, places);
        listView.setAdapter(adapter);

        // Implementing the search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used, but required to override
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String userInput = charSequence.toString().toLowerCase().trim();
                ArrayList<Place> filteredList = new ArrayList<>();

                // Loop through the places list to filter based on user input
                for (Place place : places) {
                    if (place.getPlaceName().toLowerCase().contains(userInput)) {
                        filteredList.add(place);
                    }
                }

                adapter = new CustomAdapter(PlacesActivity.this, R.layout.list_item_layout, filteredList);
                listView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used, but required to override
            }
        });
    }
}