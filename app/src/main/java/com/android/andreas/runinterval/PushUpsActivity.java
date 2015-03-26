package com.android.andreas.runinterval;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class PushUpsActivity extends ActionBarActivity implements SensorEventListener, View.OnTouchListener, GestureDetector.OnGestureListener {

    private static final String TAG = "PushUpsActivity";

    private TextView tvStateLabel;
    private TextView tvPushUps;
    private ProgressBar pbPushUps;

    private int pushUpsTotal;
    private int pushUpsRemaining;

    private boolean wasHighEnough = true;

    private GestureDetector gestureDetector;

    private SensorManager mgr;
    private Sensor lightSensor;
    private int minBrightness;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_ups);

        tvStateLabel = (TextView) findViewById(R.id.state_label);
        tvPushUps = (TextView) findViewById(R.id.number_push_ups);
        pbPushUps = (ProgressBar) findViewById(R.id.progress_push_ups);

        pushUpsTotal = 30;
        pushUpsRemaining = pushUpsTotal;
        tvPushUps.setText(String.valueOf(pushUpsRemaining));
        pbPushUps.setMax(pushUpsTotal);

        FrameLayout fl = (FrameLayout) findViewById(R.id.background);
        fl.setOnTouchListener(this);
        gestureDetector = new GestureDetector(this);

        mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = mgr.getDefaultSensor(Sensor.TYPE_LIGHT);
        mgr.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_push_ups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View _v, MotionEvent _event) {
        gestureDetector.onTouchEvent(_event);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent _event) {

        if (minBrightness == 0) {
            minBrightness = (int)Math.floor(_event.values[0] - _event.values[0] * 0.15);
            Log.i(TAG, "max brighntess value --> " + minBrightness);
        }

        // if sensor type is light, display was tapped (wasHighEnough = false), has minBrightness and brightness is higher than minBrightness
        if (_event.sensor.getType() == Sensor.TYPE_LIGHT && !wasHighEnough && minBrightness != 0 && _event.values[0] > minBrightness) {
            Log.i(TAG, "light sensor value --> " + _event.values[0]);
            wasHighEnough = true;
            pushUpsRemaining--;
            tvPushUps.setText(String.valueOf(pushUpsRemaining));
            tvStateLabel.setText("Display mit der Nase berühren");

            pbPushUps.setProgress((pushUpsTotal - pushUpsRemaining));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (wasHighEnough) {
            wasHighEnough = false;
            tvStateLabel.setText("und hoch");
            return true;
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
