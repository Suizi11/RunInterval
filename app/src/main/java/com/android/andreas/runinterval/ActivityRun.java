package com.android.andreas.runinterval;

import android.app.Activity;
import android.app.FragmentManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;


public class ActivityRun extends Activity {

    private static final String TAG = "RunInterval";
    private GoogleMap googleMap = null;
    private LocationManager locManager = null;
    private Location lastLoc = null;
    private long startTime = 0;

    private float totalDistance = 0;
    private float toIntervalDistance = 0;
    private float intervalDistance = 0;

    // Übergabeparameter
    private float intervalDistanceFull = 1000;

    @Override
    public void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_run);

        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        LocationListener locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location _location) {
                if (lastLoc == null || lastLoc.distanceTo(_location) > 1) { // every meter
                    LatLng curPosition = new LatLng(_location.getLatitude(), _location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPosition, 16));

                    // Marker-Management
                    googleMap.clear(); // deletes all markers
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(curPosition);
                    markerOptions.title("Du bist hier");
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationpointer));
                    googleMap.addMarker(markerOptions);

                    // Update Distances
                    if (lastLoc != null) {
                        totalDistance += lastLoc.distanceTo(_location);
                        intervalDistance += lastLoc.distanceTo(_location);
                        toIntervalDistance = intervalDistanceFull - intervalDistance;
                    }

                    TextView tv = null;
                    tv = (TextView)findViewById(R.id.run_text_distance_data);
                    tv.setText(String.format("%.2f", totalDistance) + " m");
                    tv = (TextView)findViewById(R.id.run_text_tointerval_data);
                    tv.setText(String.format("%.2f", toIntervalDistance) + " m");


                    lastLoc = _location; // update last location
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }
        };
        locManager.requestLocationUpdates(locManager.GPS_PROVIDER, 0, 0, locListener);

        // Create Map and zoom in
        createMapView();
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Timer
        final Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds     = seconds % 60;

                TextView tv = (TextView)findViewById(R.id.run_text_totaltime_data);
                if (seconds < 10)
                    tv.setText(minutes + ":0" + seconds);
                else
                    tv.setText(minutes + ":" + seconds);

                handler.postDelayed(this, 1000); // wait 1 second
            }
        };
        startTime = System.currentTimeMillis();
        run.run(); // start Runnable-Timer
    }

    private void createMapView(){
        try {
            if(googleMap == null){
                FragmentManager mgr = getFragmentManager();
                googleMap = ((MapFragment)mgr.findFragmentById(R.id.run_mapview)).getMap();
            }
        } catch (NullPointerException _e){
            Log.e(TAG, _e.toString());
        }
    }


}
