package edu.odu.cs441.googlemap;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import edu.odu.cs441.googlemap.model.IdentifiedLocation;
import edu.odu.cs441.googlemap.model.LocationLog;
import edu.odu.cs441.googlemap.viewmodel.LocationListViewModel;

public class CheckInActivity extends AppCompatActivity {

    private final int MY_DISTANCE_LIMIT = 30;
    public static final String MY_LONGITUDE_EXTRA_MESSAGE = "LONGITUDE_EXTRA";
    public static final String MY_LATITUDE_EXTRA_MESSAGE = "LATITUDE_EXTRA";
    public static final String MY_ADDRESS_EXTRA_MESSAGE = "ADDRESS_EXTRA";

    private LocationListViewModel locationListViewModel;

    private TextView longitudeValueTextView;
    private TextView latitudeValueTextView;
    private TextView addressValueTextView;
    private TextView locationNameLabelTextView;
    private EditText locationNameEditText;
    private Button checkInButton;
    private Button cancelButton;

    private boolean isNewIdentifiedLocation = true;
    IdentifiedLocation newIdentifiedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        Intent intent = getIntent();
        final String longitude = intent.getStringExtra(MY_LONGITUDE_EXTRA_MESSAGE);
        final String latitude = intent.getStringExtra(MY_LATITUDE_EXTRA_MESSAGE);
        final String address = intent.getStringExtra(MY_ADDRESS_EXTRA_MESSAGE);
        final Date timestamp = new Date();

        longitudeValueTextView = findViewById(R.id.activity_check_in_textview_longtitude_value);
        latitudeValueTextView = findViewById(R.id.activity_check_in_textview_latitude_value);
        addressValueTextView = findViewById(R.id.activity_check_in_textview_address_value);
        locationNameLabelTextView = findViewById(R.id.activity_check_in_textview_location_name_label);
        locationNameEditText = findViewById(R.id.activity_check_in_edittext_location_name);
        checkInButton = findViewById(R.id.activity_check_in_button_check_in);
        cancelButton = findViewById(R.id.activity_check_in_button_cancel);

        longitudeValueTextView.setText(longitude);
        latitudeValueTextView.setText(latitude);
        addressValueTextView.setText(address);

        locationListViewModel = ViewModelProviders.of(this).get(LocationListViewModel.class);


        Location location = new Location("");
        location.setLongitude(Double.valueOf(longitude));
        location.setLatitude(Double.valueOf(latitude));
        for(LocationLog locationLog : locationListViewModel.findAllLocationLogs()) {
            Location eachLocation = new Location("");
            eachLocation.setLatitude(locationLog.getLatitude());
            eachLocation.setLongitude(locationLog.getLongitude());

            float distance = location.distanceTo(eachLocation);

            if(distance < MY_DISTANCE_LIMIT) {
                newIdentifiedLocation =
                        locationListViewModel.findIdentifiedLocationByKey(locationLog.getIdentifiedLocationKey());

                locationNameEditText.setText(newIdentifiedLocation.getName());
                locationNameLabelTextView.setText("Existing Location");
                locationNameEditText.setEnabled(false);

                isNewIdentifiedLocation = false;
            }
        }

        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationNameEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(
                            CheckInActivity.this,
                            "Name of the location must be specified",
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                LocationLog locationLog = new LocationLog();
                locationLog.setLongitude(Double.valueOf(longitude));
                locationLog.setLatitude(Double.valueOf(latitude));
                locationLog.setAddress(address);
                locationLog.setTimestamp(timestamp);

                if(isNewIdentifiedLocation) {
                    IdentifiedLocation newIdentifiedLocation = new IdentifiedLocation();
                    newIdentifiedLocation.setName(locationNameEditText.getText().toString());
                    locationListViewModel.insertIdentifiedLocation(newIdentifiedLocation);
                    locationLog.setIdentifiedLocationKey(newIdentifiedLocation.getIdentifiedLocationKey());
                } else {
                    locationLog.setIdentifiedLocationKey(newIdentifiedLocation.getIdentifiedLocationKey());
                }

                locationListViewModel.insertLocationLog(locationLog);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
