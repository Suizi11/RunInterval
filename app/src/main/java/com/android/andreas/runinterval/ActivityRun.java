package com.android.andreas.runinterval;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ActivityRun extends Activity {

    private static final String TAG = "RunInterval";
    private GoogleMap googleMap;
    private LocationManager locManager;
    private Location lastLoc;
    BroadcastReceiver runDataReceiver;

    @Override
    public void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_run);

        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        LocationListener locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location _location) {
                if (lastLoc == null || lastLoc.distanceTo(_location) > 1) { // update every meter
                    LatLng curPosition = new LatLng(_location.getLatitude(), _location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPosition, 16));

                    // Marker-Management
                    googleMap.clear(); // deletes all markers
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(curPosition);
                    markerOptions.title("Du bist hier");
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationpointer));
                    googleMap.addMarker(markerOptions);

                    // Send Distance-Updates to Session Manager
                    if (lastLoc != null) {
                        SessionManager.getInstance().ranDistance((int) lastLoc.distanceTo(_location));
                    }

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

        // Broadcast-Receiver
        runDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                if (_intent.getAction() == SessionManager.BROADCAST_ACTION_NORMAL) {
                    TextView tv = null;
                    tv = (TextView)findViewById(R.id.run_text_totaltime_data);
                    int seconds = (int) (_intent.getLongExtra(SessionManager.TOTAL_TIME_KEY, 0) / 1000);
                    int minutes = seconds / 60;
                    seconds     = seconds % 60;
                    if (seconds < 10)
                        tv.setText(String.valueOf(minutes + ":0" + seconds));
                    else
                        tv.setText(String.valueOf(minutes + ":" + seconds));


                    tv = (TextView)findViewById(R.id.run_text_distance_data);
                    int totalDistance = _intent.getIntExtra(SessionManager.TOTAL_DISTANCE_KEY, 0);
                    tv.setText(String.valueOf(totalDistance) + " m");


                    tv = (TextView)findViewById(R.id.run_text_tointerval_data);
                    if (SessionManager.getInstance().getIntervalType() == IntervalType.DISTANCE) {
                        int intervalDistance = _intent.getIntExtra(SessionManager.INTERVAL_VALUE_KEY, 0);
                        tv.setText(String.valueOf(intervalDistance) + " m");

                    } else if (SessionManager.getInstance().getIntervalType() == IntervalType.TIME) {
                        seconds = (int) (_intent.getLongExtra(SessionManager.INTERVAL_VALUE_KEY, 0) / 1000);
                        minutes = seconds / 60;
                        seconds     = seconds % 60;
                        if (seconds < 10)
                            tv.setText(String.valueOf(minutes + ":0" + seconds));
                        else
                            tv.setText(String.valueOf(minutes + ":" + seconds));
                    }

                } else if (_intent.getAction() == SessionManager.BROADCAST_ACTION_EXERCISE) {
                    final ExerciseType exerciseType = (ExerciseType)_intent.getExtras().get(SessionManager.EXERCISE_TYPE_KEY);
                    int exerciseNumber = _intent.getIntExtra(SessionManager.EXERCISE_VALUE_KEY, 0);

                    Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(2000);

                    Button b = (Button)findViewById(R.id.run_button_start);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View _v) {
                            switch (_v.getId()) {
                                case R.id.run_button_start: {
                                    if (exerciseType == ExerciseType.PUSH_UPS) {
                                        Intent i = new Intent(getApplicationContext(), PushUpsActivity.class);
                                        startActivity(i);
                                    } else if(exerciseType == ExerciseType.SIT_UPS) {
                                        Intent i = new Intent(getApplicationContext(), SitUpsActivity.class);
                                        startActivity(i);
                                    }
                                } break;
                                default: Log.e(TAG, "unknown onClick-ID encountered ...");
                            }
                        }
                    });
                    b.setVisibility(View.VISIBLE);

                    TextView tv = (TextView)findViewById(R.id.run_text_intervalnotifier);
                    if (exerciseType == ExerciseType.PUSH_UPS)
                        tv.setText("Mach jetzt " + String.valueOf(exerciseNumber) + " Push-Ups");
                    else if (exerciseType == ExerciseType.SIT_UPS)
                        tv.setText("Mach jetzt " + String.valueOf(exerciseNumber) + " Sit-Ups");
                    tv.setVisibility(View.VISIBLE);

                }  else if (_intent.getAction() == SessionManager.BROADCAST_ACTION_FINISH) {
                    SessionManager.getInstance().finishedExercise();
                    Intent i = new Intent(getApplicationContext(), ActivityComplete.class);
                    startActivity(i);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SessionManager.BROADCAST_ACTION_NORMAL);
        filter.addAction(SessionManager.BROADCAST_ACTION_EXERCISE);
        filter.addAction(SessionManager.BROADCAST_ACTION_FINISH);
        LocalBroadcastManager.getInstance(this).registerReceiver(runDataReceiver, filter);

        Button b = (Button)findViewById(R.id.run_button_start);
        b.setVisibility(View.GONE);
        TextView tv = (TextView)findViewById(R.id.run_text_intervalnotifier);
        tv.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(runDataReceiver);
        super.onPause();
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
