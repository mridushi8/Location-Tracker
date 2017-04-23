package com.example.hp.map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static final String MyPREFERENCES = "MyPref" ;
    public static final String c_lat = "c_latitude";
    public static final String c_lon = "c_longitude";
    public static final String radius1 = "radius";
    SharedPreferences sharedpreferences;
    String latitude, longitude;
    Double latt, longg;
    TextView Alert;
    String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = getIntent().getExtras().getString("flag");
        Log.d("FLAAAAAAAG",flag);
        OneSignal.startInit(this).setNotificationOpenedHandler(new NotificationHandler()).init();

        setContentView(R.layout.activity_maps2);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Alert = (TextView)findViewById (R.id.alert);

        if (flag.equals("1"))
        {
            Alert.setText("PATIENT OUT OF RADIUS!!!");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Timer waitingTimer = new Timer();
        waitingTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        getStatus();
                        Log.d("ff","Polling");

                    }
                });
            }
        },0,600000);
    }

    class NotificationHandler implements OneSignal.NotificationOpenedHandler {
        public void notificationOpened(OSNotificationOpenResult result) {

            Intent i = new Intent(getApplicationContext(), MapsActivity2.class);
            i.putExtra("flag", "1");

            startActivity(i);

        }
    }







    public void getStatus() {
        RequestQueue rq = Volley.newRequestQueue(getBaseContext());
        String url = "http://182.77.97.57:80/ASA/";
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String rsp) {
                try {
                    Log.d("Json","rcd");
                    showJSON(rsp);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.d("Sending","json");
                Map<String, String> params = new HashMap<String, String>();
                String slat1 = sharedpreferences.getString(c_lat, "");
                String slong1 = sharedpreferences.getString(c_lon, "");
                String radius = sharedpreferences.getString(radius1, "");
                Log.d("abd", radius);

                params.put("bound_lat",slat1);
                params.put("bound_lon",slong1);
                params.put("bound_radius",radius);
                params.put("polling", "yes");
                params.put("flag",flag);

                return params;
            }
        };
        rq.add(sr);
    }

    private void showJSON(String json) throws JSONException {

        JSONObject reader = new JSONObject(json);
        latitude = reader.getString("latitude");
        longitude = reader.getString("longitude");
        flag = reader.getString("flag");
        Log.d("Lattt",latitude);
        Log.d("Longg", longitude);
        latt = Double.parseDouble(latitude);
        longg = Double.parseDouble(longitude);
        LatLng sydney = new LatLng( latt, longg);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //String value = sharedpreferences.getString(c_lat, "");

        // Add a marker in Sydney and move the camera

    }

 }

