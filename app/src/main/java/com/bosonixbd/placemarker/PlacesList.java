package com.bosonixbd.placemarker;

import java.io.Serializable;
import java.util.ArrayList;

public class PlacesList implements Serializable {
    private ArrayList<Place> placeArrayList;
    public void add(Place place)
    {
        placeArrayList.add(place);
    }
    public void remove(int index)
    {
        placeArrayList.remove(index);
    }
    public ArrayList<Place> getPlaceArrayList() {
        return placeArrayList;
    }

    public void setPlaceArrayList(ArrayList<Place> placeArrayList) {
        this.placeArrayList = placeArrayList;
    }
}
