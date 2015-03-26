package com.android.andreas.runinterval;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class ActivityRun extends Activity {

    private static final String TAG = "RunInterval";
    private GoogleMap googleMap = null;
    static final LatLng HAGENBERG = new LatLng(48.367361, 14.517059);
    public LatLng currentPosition = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        createMapView();
        addMarker();

        //Move the camera instantly to hamburg with a zoom of 15.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HAGENBERG, 100));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    private void createMapView(){
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if(googleMap == null){
                //googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_content)).getMap();
                FragmentManager mgr = getFragmentManager();
                googleMap = ((MapFragment)mgr.findFragmentById(R.id.run_mapview)).getMap();
            }
        } catch (NullPointerException _e){
            Log.e(TAG, _e.toString());
        }
    }

    private void addMarker(){
        /** Make sure that the map has been initialised **/
        if(null != googleMap){
            googleMap.addMarker(new MarkerOptions()
                            .position(HAGENBERG)
                            .title("Aktuelle Position")
                            .draggable(true)
            );
        }
    }
}
