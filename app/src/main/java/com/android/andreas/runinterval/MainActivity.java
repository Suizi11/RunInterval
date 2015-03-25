package com.android.andreas.runinterval;

import android.app.Activity;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener, NumberPicker.OnValueChangeListener {

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
