package com.bosonixbd.placemarker;

import java.io.Serializable;

public class Place implements Serializable{
    private String placeName;
    private double latitude;
    private double longitude;

    public Place(String placeName, double latitude, double longitude) {
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
// Getters for placeName, latitude, and longitude
    // Add getters and setters as per your requirement
}