package com.example.task91v3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class AddPlace extends AppCompatActivity {
    private static final String TAG = "Running";
    LocationManager locationManager;
    LocationListener locationListener;
    Button getButton, saveButton, showButton, showAllButton;
    TextView latlngTextView;
    EditText placeNameEditText;
    LatLng currentLocation;
    ArrayList<Marker> markerList = new ArrayList<>();
    Marker currentMarker = new Marker();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        latlngTextView = findViewById(R.id.latlngTextView);
        placeNameEditText = findViewById(R.id.placeNameEditText);
        getButton = findViewById(R.id.getButton);
        saveButton = findViewById(R.id.saveButton);
        showButton = findViewById(R.id.showMapButton);
        showAllButton = findViewById(R.id.showAllButton);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Double longitude = location.getLongitude();
                Double latitude = location.getLatitude();
                currentLocation = new LatLng(latitude, longitude);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyB5J5wSA1i6wBJpOiK8rBbuZQWxRqDI7K4");

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                LatLng latLng = place.getLatLng();
                currentMarker.setLatLng(latLng);
                latlngTextView = findViewById(R.id.latlngTextView);
                latlngTextView.setText(currentMarker.getLatLng().toString());
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = placeNameEditText.getText().toString();
                currentMarker.setName(name);
                if (name != null && name != "" && currentMarker.getLatLng() != null) {
                    Marker temp = new Marker(currentMarker.getLatLng(), currentMarker.getName());
                    markerList.add(temp);
                    currentMarker.setName("");
                    currentMarker.setLatLng(null);
                } else {
                    Toast.makeText(AddPlace.this, "Please fill out all the fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMarker.setLatLng(currentLocation);
                latlngTextView = findViewById(R.id.latlngTextView);
                latlngTextView.setText(currentMarker.getLatLng().toString());
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentMarker.getName() != null || currentMarker.getName() != "" || currentMarker.getLatLng() != null) {
                    Intent changeIntent = new Intent(com.example.task91v3.AddPlace.this, ShowSingle.class);
                    changeIntent.putExtra("name", currentMarker.getName());
                    changeIntent.putExtra("latLng", currentMarker.getLatLng());
                    startActivity(changeIntent);
                }
            }
        });

        showAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeIntent = new Intent(com.example.task91v3.AddPlace.this, ShowAll.class);
                int i = 0;
                for (Marker marker : markerList) {
                    String num = String.valueOf(i);
                    String name = "marker" + num;
                    String latLng = "latLng" + num;
                    changeIntent.putExtra(name, marker.getName());
                    changeIntent.putExtra(latLng, marker.getLatLng());
                    i++;
                }
                changeIntent.putExtra("size", markerList.size());
                startActivity(changeIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}