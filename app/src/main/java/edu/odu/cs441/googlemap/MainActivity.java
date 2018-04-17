package edu.odu.cs441.googlemap;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import edu.odu.cs441.googlemap.model.IdentifiedLocation;
import edu.odu.cs441.googlemap.model.LocationLog;
import edu.odu.cs441.googlemap.service.FetchAddressIntentService;
import edu.odu.cs441.googlemap.viewmodel.LocationListViewModel;

public class MainActivity extends AppCompatActivity {

    private final int LOCATION_UPDATE_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 200;
    private final int MY_REQUEST_CHECK_SETTINGS = 2404;

    private Location mCurrentLocation;
    private String mAddress;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private TextView mLongitudeValueTextView;
    private TextView mLatitudeValueTextView;
    private TextView mAddressValueTextView;
    private ListView mLocationListView;
    private ArrayList<String> listViewArrayList;
    private ArrayAdapter<String> listViewAdapter;

    LocationListViewModel locationListViewModel;

    private AddressResultReceiver mResultReceiver;

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mCurrentLocation);
        startService(intent);
    }

    private void updateUI(Location location) {
        mCurrentLocation = location;
        mLongitudeValueTextView.setText(String.valueOf(location.getLongitude()));
        mLatitudeValueTextView.setText(String.valueOf(location.getLatitude()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatitudeValueTextView = findViewById(R.id.activity_main_textview_latitude_value);
        mLongitudeValueTextView = findViewById(R.id.activity_main_textview_longtitude_value);
        mAddressValueTextView = findViewById(R.id.activity_main_textview_address_value);
        mLocationListView = findViewById(R.id.activity_main_listview_location);

        mResultReceiver = new AddressResultReceiver(new Handler());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        listViewArrayList = new ArrayList<>();
        listViewAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listViewArrayList);
        mLocationListView.setAdapter(listViewAdapter);

        locationListViewModel = ViewModelProviders.of(this).get(LocationListViewModel.class);

        //locationListViewModel.deleteAllLocationLogs();
        //locationListViewModel.deleteAllIdentifiedLocations();



        Button button = findViewById(R.id.activity_main_button_check_in);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mLongitudeValueTextView.getText().toString().isEmpty() &&
                        !mLatitudeValueTextView.getText().toString().isEmpty() &&
                        !mAddressValueTextView.getText().toString().isEmpty()) {
                    String longitude = mLongitudeValueTextView.getText().toString();
                    String latitude = mLatitudeValueTextView.getText().toString();
                    String address = mAddressValueTextView.getText().toString();
                    startCheckInActivity(longitude, latitude, address);
                }
            }
        });


        Button mapButton = findViewById(R.id.activity_main_button_map);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMapActivity();
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateUI(location);
                }
                if (!Geocoder.isPresent()) {
                    Toast.makeText(MainActivity.this,
                            R.string.no_geocoder_available,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                startIntentService();
            }
        };
    }

    private void startMapActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    private void startCheckInActivity(String longitude, String latitude, String address) {
        Intent intent = new Intent(this, CheckInActivity.class);
        intent.putExtra(CheckInActivity.MY_LONGITUDE_EXTRA_MESSAGE, longitude);
        intent.putExtra(CheckInActivity.MY_LATITUDE_EXTRA_MESSAGE, latitude);
        intent.putExtra(CheckInActivity.MY_ADDRESS_EXTRA_MESSAGE, address);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeLocationUpdates();

        listViewArrayList.clear();
        for(LocationLog locationLog : locationListViewModel.findAllLocationLogs()) {
            String item = "";
            IdentifiedLocation identifiedLocation =
                    locationListViewModel.findIdentifiedLocationByKey(locationLog.getIdentifiedLocationKey());

            item += "Location: " + identifiedLocation.getName() + "\n";
            item += "Address: " + locationLog.getAddress() + "\n";
            item += "Longitude: " + locationLog.getLongitude() + "\n";
            item += "Latitude: " + locationLog.getLatitude() + "\n";
            item += "Timestamp: " + locationLog.getTimestamp() + "\n";

            listViewArrayList.add(item);
        }
        listViewAdapter.notifyDataSetChanged();
    }

    private void initializeLocationUpdates() {
        if(isGooglePlayServicesAvailable(this)) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    createLocationRequest();
                    startLocationUpdates();
                }
            });
            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this,
                                    MY_REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        }
        else {
            Toast.makeText(MainActivity.this,
                    R.string.google_service_not_available,
                    Toast.LENGTH_LONG).show();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
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
                        LOCATION_UPDATE_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    public boolean isGooglePlayServicesAvailable(Context context){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_UPDATE_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                }
            }
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            mAddress = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);

            if (mAddress == null) {
                mAddress = "";
                Toast.makeText(MainActivity.this, R.string.no_address_found, Toast.LENGTH_LONG).show();
            }
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
                mAddressValueTextView.setText(mAddress);
            }
        }
    }
}
