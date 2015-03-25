package com.android.andreas.runinterval;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SeekBar sliderTotaldistance = (SeekBar)findViewById(R.id.slider_totaldistance);
        sliderTotaldistance.setOnSeekBarChangeListener(this);

        
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
}
