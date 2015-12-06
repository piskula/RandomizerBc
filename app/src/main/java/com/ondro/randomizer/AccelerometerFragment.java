package com.ondro.randomizer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Ondro on 27-Oct-15.
 */
public class AccelerometerFragment extends BaseSensorFragment implements SensorEventListener {
    private View rootView;

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
    private TextView rotVecTextView05;
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
    private TextView pressureTextView;
    private TextView ambientTemperatureTextView;
    private TextView batteryTextView01;
    private TextView batteryTextView02;
    private TextView batteryTextView03;
    private TextView batteryTextView04;
    private TextView batteryTextView05;
    private TextView batteryRefreshTextView;
    private TextView stepCounterTextView01;
    private TextView stepCounterTextView02;
    private TextView stepDetector01;
    private TextView stepDetector02;
    private TextView significantMotion;

    private BatteryThread batteryThread = new BatteryThread();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if(savedInstanceState != null){
            Toast.makeText(getActivity(), "instance loaded", Toast.LENGTH_SHORT).show();
        }
        rootView = inflater.inflate(R.layout.accelerometer_layout, container, false);

        //disable screen lock
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        InitializeViews();

        mSignificantMotionListener = new SignificantMotionTriggerListener(this.getActivity(), significantMotion);
        batteryThreadHandler.post(batteryThread);

        return rootView;
    }

    private void InitializeViews(){
        TextView availableSensors;
        TextView batteryDelayTextView;

        availableSensors = (TextView) rootView.findViewById(R.id.id_available_sensors);
        availableSensors.setText("Your API: " + Build.VERSION.SDK_INT + ", Android " + Build.VERSION.RELEASE);

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
        rotVecTextView05 = (TextView) rootView.findViewById(R.id.text_rotationvector05);
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
        pressureTextView = (TextView) rootView.findViewById(R.id.text_pressure01);
        batteryTextView01 = (TextView) rootView.findViewById(R.id.text_battery01);
        batteryTextView02 = (TextView) rootView.findViewById(R.id.text_battery02);
        batteryTextView03 = (TextView) rootView.findViewById(R.id.text_battery03);
        batteryTextView04 = (TextView) rootView.findViewById(R.id.text_battery04);
        batteryTextView05 = (TextView) rootView.findViewById(R.id.text_battery05);
        batteryRefreshTextView = (TextView) rootView.findViewById(R.id.text_microphone01);
        batteryDelayTextView = (TextView) rootView.findViewById(R.id.textView60);
        batteryDelayTextView.setText("Battery Refresh Delay: " + BATTERY_REFRESH_TIME + "ms");
        stepCounterTextView01 = (TextView) rootView.findViewById(R.id.text_stepcounter01);
        stepCounterTextView02 = (TextView) rootView.findViewById(R.id.text_stepcounter02);
        stepDetector01 = (TextView) rootView.findViewById(R.id.text_stepdetector01);
        stepDetector01.setText("::");
        stepDetector02 = (TextView) rootView.findViewById(R.id.text_stepdetector02);
        significantMotion = (TextView) rootView.findViewById(R.id.text_significantmotion01);
    }

    @Override
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
                rotVecTextView01.setText(String.valueOf(event.values[0]));
                rotVecTextView02.setText(String.valueOf(event.values[1]));
                rotVecTextView03.setText(String.valueOf(event.values[2]));
                rotVecTextView04.setText(String.valueOf(event.values[3]));
                rotVecTextView05.setText(String.valueOf(event.values[4]));
                break;
            case Sensor.TYPE_GRAVITY:
                gravityTextView01.setText(String.valueOf(event.values[0]));
                gravityTextView02.setText(String.valueOf(event.values[1]));
                gravityTextView03.setText(String.valueOf(event.values[2]));
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroscopeTextView01.setText(String.valueOf(event.values[0]));
                gyroscopeTextView02.setText(String.valueOf(event.values[1]));
                gyroscopeTextView03.setText(String.valueOf(event.values[2]));
                break;
            case Sensor.TYPE_LIGHT:
                lightTextView01.setText(String.valueOf(event.values[0]));
                break;
            case Sensor.TYPE_PROXIMITY:
                proximityTextView01.setText(String.valueOf(event.values[0]));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = event.values;
                magneticTextView01.setText(String.valueOf(event.values[0]));
                magneticTextView02.setText(String.valueOf(event.values[1]));
                magneticTextView03.setText(String.valueOf(event.values[2]));
                refreshOrientation();
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                ambientTemperatureTextView.setText(String.valueOf(event.values[0]));
                break;
            case Sensor.TYPE_PRESSURE:
                pressureTextView.setText(String.valueOf(event.values[0]));
                break;
            case Sensor.TYPE_STEP_COUNTER:
                if(stepsThisApp < 1){
                    stepsThisApp = (int) event.values[0];
                }
                stepCounterTextView01.setText(String.valueOf((int) event.values[0]));
                stepCounterTextView02.setText(String.valueOf(((int) event.values[0]) - stepsThisApp));
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                stepsDetectedThisApp++;
                stepDetector01.append("|x");
                stepDetector02.setText(String.valueOf(stepsDetectedThisApp));
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

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onResume() {
        super.onResume();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mySensorManager.registerListener(this, mySensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorLinearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorRotationVector, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorAmbientTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        if(Build.VERSION.SDK_INT >= 19){
            mySensorManager.registerListener(this, mySensorStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
            mySensorManager.registerListener(this, mySensorStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(Build.VERSION.SDK_INT >= 18){
            if(mySensorSignificantMotion != null
                    && mySensorManager.requestTriggerSensor(mSignificantMotionListener, mySensorSignificantMotion)) {
                significantMotion.setText("SignificantMotion ENABLED (Waiting..)\n");
            }
        }
    }

    public void onPause() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onPause();
        mySensorManager.unregisterListener(this);
        if(Build.VERSION.SDK_INT >= 18){
            if(mySensorSignificantMotion != null){
                mySensorManager.cancelTriggerSensor(mSignificantMotionListener, mySensorSignificantMotion);
            }
        }
    }

    private class BatteryThread implements Runnable {

        public void run() {
            batteryRefreshTextView.setText(getCounter());
            try {
                BufferedReader brCurrent = new BufferedReader(new FileReader(mCurrentBatteryFile));
                batteryTextView02.setText("Current: " + String.valueOf(((float) Integer.parseInt(brCurrent.readLine())) / 1000) + "mA");
                brCurrent.close();
            }
            catch (IOException e) {
                batteryTextView02.setText(ERROR_READING_TOAST_MSG + BATTERY_STATUS_PATH + "/" + BATTERY_CURRENT_FILE);
            }
            try{
                BufferedReader brResistance = new BufferedReader(new FileReader(mResistanceBatteryFile));
                batteryTextView04.setText("Resistance: " + String.valueOf(((float) Integer.parseInt(brResistance.readLine())) / 1000) + "mΩ");
                brResistance.close();
            }
            catch (IOException e) {
                batteryTextView04.setText(ERROR_READING_TOAST_MSG + BATTERY_STATUS_PATH + "/" + BATTERY_UEVENT_FILE);
            }
            try{
                BufferedReader brTemp = new BufferedReader(new FileReader(mTempBatteryFile));
                batteryTextView03.setText("Temperature: " + String.valueOf(((float) Integer.parseInt(brTemp.readLine())) * 0.1) + "°C");
                brTemp.close();
            }
            catch (IOException e) {
                batteryTextView03.setText(ERROR_READING_TOAST_MSG + BATTERY_STATUS_PATH + "/" + BATTERY_TEMP_FILE);
            }
            try{
                BufferedReader brVoltage = new BufferedReader(new FileReader(mVoltageBatteryFile));
                batteryTextView01.setText("Voltage: " + String.valueOf(((float) Integer.parseInt(brVoltage.readLine())) / 1000) + "mV");
                brVoltage.close();
            }
            catch (IOException e) {
                batteryTextView01.setText(ERROR_READING_TOAST_MSG + BATTERY_STATUS_PATH + "/" + BATTERY_VOLTAGE_FILE);
            }
            try{
                BufferedReader brUevent = new BufferedReader(new FileReader(mUeventBatteryFile));
                String line;
                StringBuilder str = new StringBuilder();
                while ((line = brUevent.readLine()) != null){
                    str.append(line);
                    str.append("\n");
                }
                batteryTextView05.setText(str.toString());
                brUevent.close();
            }
            catch (IOException e) {
                batteryTextView05.setText(ERROR_READING_TOAST_MSG + BATTERY_STATUS_PATH + "/" + BATTERY_UEVENT_FILE);
            }

            batteryThreadHandler.postDelayed(batteryThread, BATTERY_REFRESH_TIME);
        }
    }
}
