package com.android.andreas.runinterval;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, NumberPicker.OnValueChangeListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";
    private static final int MIN_DISTANCE_INTERVAL = 500;
    private static final int MAX_DISTANCE_INTERVAL = 2000;
    private static final int MIN_TIME_INTERVAL = 1;
    private static final int MAX_TIME_INTERVAL = 20;

    private int distance;
    private TextView distanceValueLabel;

    private IntervalType selectedIntervalType;
    private int intervalValue;
    private TextView intervalValueLabel;
    private int intervalProgress;

    private int nrPushUps;
    private int nrSitUps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        distance = 0;
        distanceValueLabel = (TextView)findViewById(R.id.total_distance_value);
        SeekBar distanceSlider = (SeekBar) findViewById(R.id.total_distance_slider);
        onProgressChanged(distanceSlider, 0, false);
        distanceSlider.setOnSeekBarChangeListener(this);

        selectedIntervalType = IntervalType.DISTANCE;
        intervalValueLabel = (TextView)findViewById(R.id.interval_value);
        RadioGroup rbIntervalType = (RadioGroup) findViewById(R.id.radio_interval_type);
        rbIntervalType.setOnCheckedChangeListener(this);
        SeekBar intervalSlider = (SeekBar) findViewById(R.id.interval_slider);
        onProgressChanged(intervalSlider, 0, false);
        intervalSlider.setOnSeekBarChangeListener(this);

        NumberPicker npPushUps = (NumberPicker)findViewById(R.id.np_push_ups);
        npPushUps.setMinValue(0);
        npPushUps.setMaxValue(50);
        npPushUps.setWrapSelectorWheel(false);
        npPushUps.setOnValueChangedListener(this);

        NumberPicker npSitUps = (NumberPicker)findViewById(R.id.np_sit_ups);
        npSitUps.setMinValue(0);
        npSitUps.setMaxValue(50);
        npSitUps.setWrapSelectorWheel(false);
        npSitUps.setOnValueChangedListener(this);

        Button b = (Button)findViewById(R.id.button_start);
        b.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    /** Slider callbacks */
    @Override
    public void onProgressChanged(SeekBar _seekBar, int _progress, boolean _fromUser) {

        if (_seekBar.getId() == R.id.total_distance_slider) {
            _progress += 1; // to ensure a value bigger than 0

            distance = _progress * 100;
            Log.i(TAG, String.valueOf(distance) + "   " + String.valueOf(_fromUser));
            float labelValue = distance / 1000f;
            distanceValueLabel.setText(labelValue + " km");

        } else if (_seekBar.getId() == R.id.interval_slider) {
            intervalProgress = _progress;

            if (selectedIntervalType == IntervalType.DISTANCE) {
                int distanceInterval = Math.round((float)(MAX_DISTANCE_INTERVAL - MIN_DISTANCE_INTERVAL) * ((float)_progress / 100) + MIN_DISTANCE_INTERVAL);
                intervalValue = distanceInterval;
                intervalValueLabel.setText(String.valueOf(distanceInterval) + " m");
            } else if (selectedIntervalType == IntervalType.TIME) {
                int timeInterval = Math.round((float)(MAX_TIME_INTERVAL - MIN_TIME_INTERVAL) * ((float)_progress / 100) + MIN_TIME_INTERVAL);
                intervalValue = timeInterval;
                intervalValueLabel.setText(String.valueOf(timeInterval) + " min");
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }


    /** Number-Picker callbacks */
    @Override
    public void onValueChange(NumberPicker _picker, int _oldVal, int _newVal) {
        if (_picker.getId() == R.id.np_push_ups) {
            nrPushUps = _newVal;
        } else if (_picker.getId() == R.id.np_sit_ups) {
            nrSitUps = _newVal;
        }
    }


    /** RadioGroup callbacks */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId) {
            case R.id.radio_interval_distance:
                selectedIntervalType = IntervalType.DISTANCE;
                break;

            case R.id.radio_interval_time:
                selectedIntervalType = IntervalType.TIME;
                break;

            default:
        }

        onProgressChanged((SeekBar)findViewById(R.id.interval_slider), intervalProgress, false);
    }


    /** Button callbacks */
    @Override
    public void onClick(View _v) {
        if (_v.getId() == R.id.button_start) {
            if (SessionManager.getInstance().setUpNewSession(distance, selectedIntervalType, intervalValue, nrPushUps, nrSitUps)) {
                Intent i = new Intent(this, ActivityRun.class);
                startActivity(i);
            } else {
                // TODO show message, that an active session is running
            }
        }
    }
}
