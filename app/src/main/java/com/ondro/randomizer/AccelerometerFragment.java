package com.ondro.randomizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * Created by Ondro on 27-Oct-15.
 */
public class AccelerometerFragment extends Fragment implements SensorEventListener {
    private View rootView;
    private SensorManager mySensorManager;

    private Sensor mySensorAccelerometer;
    private Sensor mySensorLinearAccelerometer;
    private Sensor mySensorRotationVector;
    private Sensor mySensorGravity;
    private Sensor mySensorGyroscope;
    private Sensor mySensorLight;
    private Sensor mySensorProximity;
    private Sensor mySensorMagnetic;
    private Sensor mySensorTemperature;
    private Sensor mySensorAmbientTemperature;

    private TextView accTextView01;
    private TextView accTextView02;
    private TextView accTextView03;
    private TextView linAccTextView01;
    private TextView linAccTextView02;
    private TextView linAccTextView03;
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
    private TextView magneticTextView01;
    private TextView magneticTextView02;
    private TextView magneticTextView03;
    private TextView orientationTextView01;
    private TextView orientationTextView02;
    private TextView orientationTextView03;
    private TextView temperatureTextView;
    private TextView ambientTemperatureTextView;
    private TextView microphoneTextView;

    private float[] mGravity;
    private float[] mGeomagnetic;

    private TextView batteryTextView01;
    private TextView batteryTextView02;
    private TextView batteryTextView03;
    private TextView batteryTextView04;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.accelerometer_layout, container, false);
        mySensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);

        //disable screen lock
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getActivity().registerReceiver(batteryBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        InitializeSensors();
        InitializeViews();

        return rootView;
    }

    private void InitializeSensors(){
        mySensorAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorLinearAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mySensorRotationVector = mySensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mySensorGravity = mySensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mySensorGyroscope = mySensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mySensorLight = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mySensorProximity = mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mySensorMagnetic = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mySensorTemperature = mySensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        mySensorAmbientTemperature = mySensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
    }

    private void InitializeViews(){
        accTextView01 = (TextView) rootView.findViewById(R.id.text_accelerometer01);
        accTextView02 = (TextView) rootView.findViewById(R.id.text_accelerometer02);
        accTextView03 = (TextView) rootView.findViewById(R.id.text_accelerometer03);
        linAccTextView01 = (TextView) rootView.findViewById(R.id.text_linearaccelerometer01);
        linAccTextView02 = (TextView) rootView.findViewById(R.id.text_linearaccelerometer02);
        linAccTextView03 = (TextView) rootView.findViewById(R.id.text_linearaccelerometer03);
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
        magneticTextView01 = (TextView) rootView.findViewById(R.id.text_magnetic01);
        magneticTextView02 = (TextView) rootView.findViewById(R.id.text_magnetic02);
        magneticTextView03 = (TextView) rootView.findViewById(R.id.text_magnetic03);
        orientationTextView01 = (TextView) rootView.findViewById(R.id.text_orientation01);
        orientationTextView02 = (TextView) rootView.findViewById(R.id.text_orientation02);
        orientationTextView03 = (TextView) rootView.findViewById(R.id.text_orientation03);
        ambientTemperatureTextView = (TextView) rootView.findViewById(R.id.text_ambienttemperature01);
        temperatureTextView = (TextView) rootView.findViewById(R.id.text_temperature01);
        batteryTextView01 = (TextView) rootView.findViewById(R.id.text_battery01);
        batteryTextView02 = (TextView) rootView.findViewById(R.id.text_battery02);
        batteryTextView03 = (TextView) rootView.findViewById(R.id.text_battery03);
        batteryTextView04 = (TextView) rootView.findViewById(R.id.text_battery04);
        microphoneTextView = (TextView) rootView.findViewById(R.id.text_microphone01);
    }

    public void onSensorChanged(SensorEvent event){
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values;
                accTextView01.setText("" + event.values[0]);
                accTextView02.setText("" + event.values[1]);
                accTextView03.setText("" + event.values[2]);
                refreshOrientation();
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                linAccTextView01.setText("" + event.values[0]);
                linAccTextView02.setText("" + event.values[1]);
                linAccTextView03.setText("" + event.values[2]);
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
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = event.values;
                magneticTextView01.setText("" + event.values[0]);
                magneticTextView02.setText("" + event.values[1]);
                magneticTextView03.setText("" + event.values[2]);
                refreshOrientation();
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                ambientTemperatureTextView.setText("Y" + event.values[0]);
                break;
            case Sensor.TYPE_TEMPERATURE:
                temperatureTextView.setText("Y" + event.values[0]);
                break;
        }
    }

    private void refreshOrientation(){
        if(mGravity != null && mGeomagnetic != null){
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                orientationTextView01.setText("" + orientation[0] * 180 / Math.PI);
                orientationTextView02.setText("" + orientation[1] * 180 / Math.PI);
                orientationTextView03.setText("" + orientation[2] * 180 / Math.PI);
            }
        }
    }

    private BroadcastReceiver batteryBroadcastReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status2 = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

            int level = -1;
            if (currentLevel >= 0 && scale > 0) {
                level = (currentLevel * 100) / scale;
            }
            batteryTextView01.setText("Battery Level Remaining: " + level + "%");
            batteryTextView02.setText("current level " + currentLevel +
                    " from " + scale);
            batteryTextView03.setText("status " + status2 + ", health " + health + ", plugged " + plugged);
            batteryTextView04.setText("voltage " + voltage + ", temperature " + ((float) temperature)/10 + "°C");
        }
    };

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onResume() {
        super.onResume();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().registerReceiver(batteryBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        mySensorManager.registerListener(this, mySensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorLinearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorRotationVector, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorAmbientTemperature, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onPause();
        getActivity().unregisterReceiver(batteryBroadcastReceiver);
        mySensorManager.unregisterListener(this);
    }
}
