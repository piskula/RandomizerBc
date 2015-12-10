package com.ondro.randomizer.mainfragments;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ondro.randomizer.SignificantMotionTriggerListener;

import java.io.File;

/**
 * Created by Ondro on 05-Dec-15.
 */
public class BaseSensorFragment extends Fragment {
    public static final long BATTERY_REFRESH_TIME = 300;
    public static final String BATTERY_STATUS_PATH = "/sys/class/power_supply/battery";
    public static final String BATTERY_CURRENT_FILE = "current_now";
    public static final String BATTERY_RESISTANCE_FILE = "resistance";
    public static final String BATTERY_UEVENT_FILE = "uevent";
    public static final String BATTERY_TEMP_FILE = "temp";
    public static final String BATTERY_VOLTAGE_FILE = "voltage_now";

    public static final String ERROR_READING_TOAST_MSG = "Error occured while reading values from ";

    protected SensorManager mySensorManager;

    protected Sensor mySensorAccelerometer;
    protected Sensor mySensorLinearAccelerometer;
    protected Sensor mySensorRotationVector;
    protected Sensor mySensorGravity;
    protected Sensor mySensorGyroscope;
    protected Sensor mySensorLight;
    protected Sensor mySensorProximity;
    protected Sensor mySensorMagnetic;
    protected Sensor mySensorAmbientTemperature;
    protected Sensor mySensorStepCounter;
    protected int stepsThisApp = 0;
    protected int stepsDetectedThisApp = 0;
    protected Sensor mySensorStepDetector;
    protected Sensor mySensorSignificantMotion;
    protected SignificantMotionTriggerListener mSignificantMotionListener;

    protected float[] mGravity;
    protected float[] mGeomagnetic;

    protected File mCurrentBatteryFile;
    protected File mResistanceBatteryFile;
    protected File mUeventBatteryFile;
    protected File mTempBatteryFile;
    protected File mVoltageBatteryFile;
    protected Handler batteryThreadHandler = new Handler();
    //protected BatteryThread batteryThread = new BatteryThread();
    protected boolean isFirst;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mySensorManager = (SensorManager) getActivity().getSystemService(FragmentActivity.SENSOR_SERVICE);
        InitializeSensors();
        assignBatteryFiles();
        return null;
    }

    private void assignBatteryFiles(){
        mCurrentBatteryFile = new File(BATTERY_STATUS_PATH, BATTERY_CURRENT_FILE);
        mResistanceBatteryFile = new File(BATTERY_STATUS_PATH, BATTERY_RESISTANCE_FILE);
        mTempBatteryFile = new File(BATTERY_STATUS_PATH, BATTERY_TEMP_FILE);
        mUeventBatteryFile = new File(BATTERY_STATUS_PATH, BATTERY_UEVENT_FILE);
        mVoltageBatteryFile = new File(BATTERY_STATUS_PATH, BATTERY_VOLTAGE_FILE);
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
        if(Build.VERSION.SDK_INT >= 14){
            mySensorAmbientTemperature = mySensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }
        if(Build.VERSION.SDK_INT >= 19){
            mySensorStepCounter = mySensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            mySensorStepDetector = mySensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }
        if(Build.VERSION.SDK_INT >= 18){
            mySensorSignificantMotion = mySensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        }
    }

    //Return different String to see how fast are changes displayed
    protected String getCounter(){
        if(isFirst){
            isFirst = !isFirst;
            return "RAZ";
        }
        else{
            isFirst = !isFirst;
            return "DVA";
        }
    }
}
