package edu.odu.cs441.googlemap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddMarkerActivity extends AppCompatActivity {

    public static final String MY_LATITUDE_INTENT_EXTRA_LABEL = "MARKER_LATITUDE";
    public static final String MY_LONGITUDE_INTENT_EXTRA_LABEL = "MARKER_LONGITUDE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);

        Intent intent = getIntent();


        TextView latitudeValueTextView = findViewById(R.id.activity_add_marker_textview_latitude_value);
        TextView longitudeValueTextView = findViewById(R.id.activity_add_marker_textview_longtitude_value);

        latitudeValueTextView.setText(intent.getStringExtra(MY_LATITUDE_INTENT_EXTRA_LABEL));
        longitudeValueTextView.setText(intent.getStringExtra(MY_LONGITUDE_INTENT_EXTRA_LABEL));

        Button addMarkerButton = findViewById(R.id.activity_add_marker_button_add_marker);
        Button cancelButton = findViewById(R.id.activity_add_marker_button_cancel);
        final EditText titleEditText = findViewById(R.id.activity_add_marker_edittext_title);

        addMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();

                if(title.trim().isEmpty()) {
                    Toast.makeText(AddMarkerActivity.this, "Specify the title", Toast.LENGTH_LONG).show();
                } else {
                    Intent data = new Intent();
                    data.putExtra(MapsActivity.MY_TITLE_STRING_EXTRA_LABEL, title);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
