package com.ondro.randomizer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Ondro on 27-Oct-15.
 */
public class AccelerometerFragment extends Fragment implements SensorEventListener {
    private View rootView;
    private SensorManager mySensorManager;

    private Sensor mySensorAccelerometer;
    private Sensor mySensorRotationVector;
    private Sensor mySensorGravity;
    private Sensor mySensorGyroscope;
    private Sensor mySensorLight;
    private Sensor mySensorProximity;

    private TextView accTextView01;
    private TextView accTextView02;
    private TextView accTextView03;
    private TextView rotVecTextView01;
    private TextView rotVecTextView02;
    private TextView rotVecTextView03;
    private TextView rotVecTextView04;
    private TextView gravityTextView01;
    private TextView gravityTextView02;
    private TextView gravityTextView03;
    private TextView gyroscopeTextView01;
    private TextView gyroscopeTextView02;
    private TextView gyroscopeTextView03;
    private TextView lightTextView01;
    private TextView proximityTextView01;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.accelerometer_layout, container, false);
        mySensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        //disable screen lock
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mySensorAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorRotationVector = mySensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mySensorGravity = mySensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mySensorGyroscope = mySensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mySensorLight = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mySensorProximity = mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        InitializeViews();
        return rootView;
    }

    private void InitializeViews(){
        accTextView01 = (TextView) rootView.findViewById(R.id.text_accelerometer01);
        accTextView02 = (TextView) rootView.findViewById(R.id.text_accelerometer02);
        accTextView03 = (TextView) rootView.findViewById(R.id.text_accelerometer03);
        rotVecTextView01 = (TextView) rootView.findViewById(R.id.text_rotationvector01);
        rotVecTextView02 = (TextView) rootView.findViewById(R.id.text_rotationvector02);
        rotVecTextView03 = (TextView) rootView.findViewById(R.id.text_rotationvector03);
        rotVecTextView04 = (TextView) rootView.findViewById(R.id.text_rotationvector04);
        gravityTextView01 = (TextView) rootView.findViewById(R.id.text_gravity01);
        gravityTextView02 = (TextView) rootView.findViewById(R.id.text_gravity02);
        gravityTextView03 = (TextView) rootView.findViewById(R.id.text_gravity03);
        gyroscopeTextView01 = (TextView) rootView.findViewById(R.id.text_gyroscope01);
        gyroscopeTextView02 = (TextView) rootView.findViewById(R.id.text_gyroscope02);
        gyroscopeTextView03 = (TextView) rootView.findViewById(R.id.text_gyroscope03);
        lightTextView01 = (TextView) rootView.findViewById(R.id.text_light01);
        proximityTextView01 = (TextView) rootView.findViewById(R.id.text_proximity01);
    }

    public void onSensorChanged(SensorEvent event){
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                accTextView01.setText("" + event.values[0]);
                accTextView02.setText("" + event.values[1]);
                accTextView03.setText("" + event.values[2]);
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                rotVecTextView01.setText("" + event.values[0]);
                rotVecTextView02.setText("" + event.values[1]);
                rotVecTextView03.setText("" + event.values[2]);
                rotVecTextView04.setText("" + event.values[3]);
                break;
            case Sensor.TYPE_GRAVITY:
                gravityTextView01.setText("" + event.values[0]);
                gravityTextView02.setText("" + event.values[1]);
                gravityTextView03.setText("" + event.values[2]);
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroscopeTextView01.setText("" + event.values[0]);
                gyroscopeTextView02.setText("" + event.values[1]);
                gyroscopeTextView03.setText("" + event.values[2]);
                break;
            case Sensor.TYPE_LIGHT:
                lightTextView01.setText("" + event.values[0]);
                break;
            case Sensor.TYPE_PROXIMITY:
                proximityTextView01.setText("" + event.values[0]);
                break;
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onResume() {
        super.onResume();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mySensorManager.registerListener(this, mySensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorRotationVector, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onPause();
        mySensorManager.unregisterListener(this);
    }
}
