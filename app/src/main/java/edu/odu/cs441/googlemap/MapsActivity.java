package edu.odu.cs441.googlemap;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import edu.odu.cs441.googlemap.model.LocationLog;
import edu.odu.cs441.googlemap.viewmodel.LocationListViewModel;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final int SET_MYLOCATION_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 350;
    private final int GET_MYLOCATION_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 450;
    private final int ADD_MARKER_ACTIVITY_REQUEST_CODE = 102;
    public static final String MY_TITLE_STRING_EXTRA_LABEL = "NEW_MARKER_STRING";

    GoogleMap mMap;
    UiSettings mapUISettings;
    Location mMyLocation;
    LatLng mNewLatLng;

    LocationListViewModel locationListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationListViewModel = ViewModelProviders.of(this).get(LocationListViewModel.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMyLocationEnabled();
        mapUISettings = mMap.getUiSettings();
        mapUISettings.setMyLocationButtonEnabled(true);
        mapUISettings.setCompassEnabled(true);
        mapUISettings.setZoomControlsEnabled(true);

        getMyLocation();
        if(mMyLocation != null) {
            LatLng myLocation = new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(myLocation).title("My Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        }

        for(LocationLog locationLog : locationListViewModel.findAllLocationLogs()) {
            LatLng eachLocation = new LatLng(locationLog.getLatitude(), locationLog.getLongitude());
            mMap.addMarker(new MarkerOptions().position(eachLocation).title(locationLog.getAddress()));
        }


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mNewLatLng = latLng;
                startAddMarkerActivityForResult(latLng);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {

            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {

            }
        });
    }

    private void startAddMarkerActivityForResult(LatLng latLng) {
        Intent intent = new Intent(this, AddMarkerActivity.class);
        intent.putExtra(AddMarkerActivity.MY_LATITUDE_INTENT_EXTRA_LABEL, String.valueOf(latLng.latitude));
        intent.putExtra(AddMarkerActivity.MY_LONGITUDE_INTENT_EXTRA_LABEL, String.valueOf(latLng.longitude));
        startActivityForResult(intent, ADD_MARKER_ACTIVITY_REQUEST_CODE);
    }

    private void getMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        GET_MYLOCATION_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria;
            criteria = new Criteria();

            mMyLocation = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));

        }
    }

    private void setMyLocationEnabled() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        SET_MYLOCATION_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SET_MYLOCATION_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setMyLocationEnabled();
                }
            }

            case GET_MYLOCATION_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMyLocation();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ADD_MARKER_ACTIVITY_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String title = data.getStringExtra(MY_TITLE_STRING_EXTRA_LABEL);

                mMap.addMarker(new MarkerOptions()
                        .position(mNewLatLng)
                        .title(title).draggable(true));
            }
        }
    }
}


