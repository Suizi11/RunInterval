package com.android.andreas.runinterval;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;


public class SitUpsActivity extends ActionBarActivity implements SensorEventListener {

    private enum Position {
        DOWN,
        MOVING,
        UP
    }

    private static final String TAG = "PushUpsActivity";

    private TextView tvStateLabel;
    private TextView tvSitUps;
    private ProgressBar pbSitUps;

    private int sitUpsTotal;
    private int sitUpsRemaining;

    private Position pos;

    private SensorManager mgr;
    private Sensor accelerometerSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sit_ups);

        tvStateLabel = (TextView) findViewById(R.id.state_label);
        tvSitUps = (TextView) findViewById(R.id.number_sit_ups);
        pbSitUps = (ProgressBar) findViewById(R.id.progress_sit_ups);

        sitUpsTotal = SessionManager.getInstance().getSitupsNumber();
        sitUpsRemaining = sitUpsTotal;
        tvSitUps.setText(String.valueOf(sitUpsRemaining));
        pbSitUps.setMax(sitUpsTotal);

        pos = Position.DOWN;

        mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mgr.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sit_ups, menu);
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
    public void onSensorChanged(SensorEvent _event) {

        if (_event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            if (pos == Position.DOWN && _event.values[0] > 8) {
                pos = Position.UP;
                sitUpsRemaining--;
                tvSitUps.setText(String.valueOf(sitUpsRemaining));
                tvStateLabel.setText("runter");

                pbSitUps.setProgress((sitUpsTotal - sitUpsRemaining));

                if (sitUpsRemaining == 0) {
                    SessionManager.getInstance().finishedExercise();
                    finish();
                }

            } else if (pos == Position.UP && _event.values[0] < 4) {
                pos = Position.DOWN;

                tvStateLabel.setText("hoch");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
