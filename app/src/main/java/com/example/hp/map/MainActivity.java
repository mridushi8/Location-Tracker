package com.example.hp.map;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import android.location.Address;
import android.location.Geocoder;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends AppCompatActivity {

    PlaceAutocompleteFragment autocompleteFragment, autocompleteFragment1;
    double lat, log;
    String source;
    String slat1,slong1;
    TextView radius;

    static String TAG = "MainActivity";

    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        i = new Intent(MainActivity.this, MapsActivity.class);

        radius = (TextView) findViewById (R.id.radius);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("radius", Double.valueOf(radius.getText().toString()));
                startActivity(i);
            }
        });
        autocompleteFragment =
                (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);autocompleteFragment.setBoundsBias(new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466)));



        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                String location = (String) place.getAddress();
                Geocoder geocoder = new Geocoder(getBaseContext());
                try {
                    List addressList = geocoder.getFromLocationName(location, 1);
                    if (addressList != null && addressList.size()>0){
                        Address address = (Address) addressList.get(0);
                        lat = address.getLatitude();
                        log = address.getLongitude();
                        slat1=Double.toString(lat);
                        slong1=Double.toString(log);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        i.putExtra("latitude", latLng.latitude);
                        i.putExtra("longitude", latLng.longitude);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                source = (String) place.getName();

            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }


        });


    }
}
