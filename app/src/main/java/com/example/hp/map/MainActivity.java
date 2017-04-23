package com.example.hp.map;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import android.location.Address;
import android.location.Geocoder;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.content.Context;

import android.content.SharedPreferences;


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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static com.example.hp.map.R.id.map;


public class MainActivity extends AppCompatActivity {

    PlaceAutocompleteFragment autocompleteFragment, autocompleteFragment1;
    double lat, log;
    String source;
    String slat1,slong1;
    TextView radius;
    public static final String MyPREFERENCES = "MyPref" ;
    public static final String c_lat = "c_latitude";
    public static final String c_lon = "c_longitude";
    public static final String radius1 = "radius";
    SharedPreferences sharedpreferences;
    static String TAG = "MainActivity";
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    String radio;

    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        i = new Intent(MainActivity.this, MapsActivity.class);



        radius = (EditText) findViewById (R.id.radius);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                radio = radius.getText().toString();
                editor.putString(radius1,radio );
                Log.d("final", radio);
                editor.commit();



                i.putExtra("radius", Double.valueOf(radius.getText().toString()));
                startActivity(i);
                RequestQueue rq = Volley.newRequestQueue(getBaseContext());
                String url = "http://182.77.97.57:80/ASA/";
                StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String rsp) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("bound_lat",slat1);
                        params.put("bound_lon",slong1);
                        params.put("bound_radius",radius.getText().toString());
                        params.put("polling", "no");
                        params.put("flag","0");


                        return params;
                    }
                };
                rq.add(sr);
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
                Context context = getApplicationContext();
                SharedPreferences.Editor editor = sharedpreferences.edit();

                try {
                    List addressList = geocoder.getFromLocationName(location, 1);
                    if (addressList != null && addressList.size()>0){
                        Address address = (Address) addressList.get(0);
                        lat = address.getLatitude();
                        log = address.getLongitude();
                        slat1=Double.toString(lat);
                        slong1=Double.toString(log);
                        Log.d("Lat",slat1);
                        Log.d("Long", slong1);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        i.putExtra("latitude", latLng.latitude);
                        i.putExtra("longitude", latLng.longitude);
                        radio = radius.getText().toString();
                        Log.d("initial", radio);
                        editor.putString(c_lat,slat1 );
                        editor.putString(c_lon, slong1);
                        editor.putString(radius1,radio );
                        Log.d("final", radio);

                        Log.d("abc","Saved to SP");
                        editor.commit();

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
