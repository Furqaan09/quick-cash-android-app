package com.example.quickcash.maps;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import com.example.quickcash.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.quickcash.databinding.ActivityMapsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // Declare variables
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private DatabaseReference jobRef;
    private HashMap<String, Marker> existingMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize markers hashmap and job reference
        existingMarkers = new HashMap<>();
        jobRef = FirebaseDatabase.getInstance().getReference("job_posts");
    }

    // This method is used to get the Lat and Long of a location using Geocoder API
    private LatLng getLatLng(String location) {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // This method displays the map when it gets ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // We will use our custom window to display job details when marker is clicked
        mMap.setInfoWindowAdapter(new JobInfoWindowAdapter(this));
        placeMarkers();
    }

    /*
     * This method is used to get all the job locations from firebase, get their Lat and Long,
     * initialize the marker info window and place the markers on the map
     */
    public void placeMarkers() {
        jobRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    String jobId = jobSnapshot.getKey();
                    String jobTitle = jobSnapshot.child("jobTitle").getValue(String.class);
                    String companyName = jobSnapshot.child("companyName").getValue(String.class);
                    String jobType = jobSnapshot.child("jobType").getValue(String.class);
                    String location = jobSnapshot.child("location").getValue(String.class);

                    LatLng locationLatLng = getLatLng(location);

                    if (!existingMarkers.containsKey(jobId)) {
                        String jobSnippet = getSnippet(companyName, jobType);
                        Marker marker = null;
                        if (locationLatLng != null) {
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(locationLatLng).title(jobTitle).snippet(jobSnippet));
                        }
                        existingMarkers.put(jobId, marker);
                    }
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(existingMarkers.values().iterator().next().getPosition(), 5));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MapsActivity.this, "Failed to load job postings", Toast.LENGTH_SHORT).show();
            }
        });

        // Set on click listener for the markers
        mMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });
    }

    // This method returns the snippet String for the marker
    public String getSnippet(String companyName, String jobType) {
        return "Company: "+companyName+"\nType: "+jobType;
    }

    // This method returns the Google map
    public GoogleMap getMap() {
        return this.mMap;
    }
}