package com.android.andreas.runinterval;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener, NumberPicker.OnValueChangeListener {

    NumberPicker npPushups = null;
    SeekBar sliderTotalDistance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sliderTotalDistance = (SeekBar)findViewById(R.id.slider_totaldistance);
        sliderTotalDistance.setOnSeekBarChangeListener(this);

        npPushups = (NumberPicker)findViewById(R.id.numberpicker_pushups);
        npPushups.setMinValue(1);
        npPushups.setMaxValue(50);
        npPushups.setWrapSelectorWheel(false);
        npPushups.setOnValueChangedListener(this);

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

    @Override
    public void onProgressChanged(SeekBar _seekBar, int _progress, boolean _fromUser) {
        TextView tv = (TextView)findViewById(R.id.text_totaldistance_data);
        tv.setText(_progress + " km");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onValueChange(NumberPicker _picker, int _oldVal, int _newVal) {

    }
}
