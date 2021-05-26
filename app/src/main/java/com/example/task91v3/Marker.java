package com.example.task91v3;

import com.google.android.gms.maps.model.LatLng;

public class Marker {
    private LatLng latLng;
    private String name;

    public Marker(LatLng latLng, String name) {
        this.latLng = latLng;
        this.name = name;
    }

    public Marker() {
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
