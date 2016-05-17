package com.ondro.randomizer.streaming;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ondro.randomizer.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ondrej.oravcok on 14.4.2016.
 */
public class BackgroundSetUpActivityEach extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    public final String TAG = "BackgroundSetUpActivityEach";

    private static Button btnService;
    private Context mContext;
    private Spinner spinnerTime;
    private static TextView statusText1;
    private static TextView statusText2;
    private static TextView statusText3;
    private Integer[] count = new Integer[]{100,500,1000,2000,3000,5000,10000};

    private static boolean stopThreadFlag;
    private static Thread myThread;

    private PowerManager.WakeLock wakeLock;
    private SharedPreferences sharedPref;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private RadioGroup senzorRadio;
    private int[] counters;

    private float lightValue;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background_activity);
        mContext = this;

        btnService = (Button) findViewById(R.id.btnService);
        spinnerTime = (Spinner) findViewById(R.id.spinner_time);
        statusText1 = (TextView) findViewById(R.id.status_text1);
        statusText2 = (TextView) findViewById(R.id.status_text2);
        statusText3 = (TextView) findViewById(R.id.status_text3);
        senzorRadio = (RadioGroup) findViewById(R.id.senzor_radio);
        senzorRadio.check(R.id.high_freq);

        btnService.setOnClickListener(this);
        statusText1.setText("");
        statusText2.setText("");
        statusText3.setText("");
        spinnerTime.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, count));

        mySensorManager = (SensorManager) getSystemService(FragmentActivity.SENSOR_SERVICE);
        InitializeSensors();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnService:
                if(stopThreadFlag){
                    PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                    wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
                    wakeLock.acquire();
                    if(myThread == null){
                        myThread = new Thread(myStreamingBackgroundTask);
                    }
                    stopThreadFlag = false;
                    btnService.setText("Stop");
                    myThread.start();
                }
                else {
                    stopThreadFlag = true;
                    btnService.setText("Start");
                    if(wakeLock != null)
                        wakeLock.release();
                }
                break;
        }
    }

    private String getFileTitle(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        return dateFormat.format(cal.getTime()) + ".txt";
    }

    private SensorManager mySensorManager;

    private Sensor mySensorAccelerometer;
    private Sensor mySensorLinearAccelerometer;
    private Sensor mySensorRotationVector;
    private Sensor mySensorGravity;
    private Sensor mySensorGyroscope;
    private Sensor mySensorLight;
    private Sensor mySensorProximity;
    private Sensor mySensorMagnetic;

    private void InitializeSensors(){
        mySensorAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorLinearAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mySensorRotationVector = mySensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mySensorGravity = mySensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mySensorGyroscope = mySensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mySensorLight = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mySensorProximity = mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mySensorMagnetic = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void registerListeners() {
        switch(senzorRadio.getCheckedRadioButtonId()){
            case R.id.low_freq:
                mySensorManager.registerListener(this, mySensorLight, SensorManager.SENSOR_DELAY_NORMAL);
                mySensorManager.registerListener(this, mySensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case R.id.mid_freq:
                mySensorManager.registerListener(this, mySensorRotationVector, SensorManager.SENSOR_DELAY_NORMAL);
                mySensorManager.registerListener(this, mySensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case R.id.high_freq:
                mySensorManager.registerListener(this, mySensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                mySensorManager.registerListener(this, mySensorLinearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                mySensorManager.registerListener(this, mySensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
                mySensorManager.registerListener(this, mySensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
                break;
        }
    }

    private String getTimeStamp(){
        return dateFormat.format(Calendar.getInstance().getTime()) + "-";
    }

    private String getValues(int numberOfValues, float[] values){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < numberOfValues; i++){
            sb.append(Float.toString(values[i]));
            sb.append(",");
        }
        sb.append("|\n");

        return sb.toString();
    }

    private void actualizeAccelerometer(float[] value){
        try{
            bw.append(getTimeStamp());
            bw.append("acc:");
            bw.append(getValues(3, value));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void actualizeLinearAcceleration(float[] value){
        try{
            bw.append(getTimeStamp());
            bw.append("linacc:");
            bw.append(getValues(3, value));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void actualizeMagnetic(float[] value){
        try{
            bw.append(getTimeStamp());
            bw.append("magn:");
            bw.append(getValues(3, value));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void actualizeGyroscope(float[] value){
        try{
            bw.append(getTimeStamp());
            bw.append("gyro:");
            bw.append(getValues(3, value));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void actualizeProximity(float[] value){
        try{
            bw.append(getTimeStamp());
            bw.append("proximity:");
            bw.append(getValues(1, value));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void actualizeLight(float[] value){
        try{
            bw.append(getTimeStamp());
            bw.append("light:");
            bw.append(getValues(1, value));
        }
        catch (IOException e){
            e.printStackTrace();
        }
        lightValue = value[0];
    }

    private void actualizeRotationVector(float[] value){
        try{
            bw.append(getTimeStamp());
            bw.append("rotvec:");
            bw.append(getValues(4, value));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void actualizeGravity(float[] value){
        try{
            bw.append(getTimeStamp());
            bw.append("gravity:");
            bw.append(getValues(3, value));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                actualizeAccelerometer(sensorEvent.values);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                actualizeLinearAcceleration(sensorEvent.values);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                actualizeMagnetic(sensorEvent.values);
                break;
            case Sensor.TYPE_GYROSCOPE:
                actualizeGyroscope(sensorEvent.values);
                break;
            case Sensor.TYPE_PROXIMITY:
                actualizeProximity(sensorEvent.values);
                break;
            case Sensor.TYPE_LIGHT:
                if(sensorEvent.values[0] == lightValue)
                    return;
                actualizeLight(sensorEvent.values);
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                actualizeRotationVector(sensorEvent.values);
                break;
            case Sensor.TYPE_GRAVITY:
                actualizeGravity(sensorEvent.values);
                break;
        }

        counters[sensorEvent.sensor.getType()]--;
        if(counters[sensorEvent.sensor.getType()] <= 0){
            mySensorManager.unregisterListener(this, sensorEvent.sensor);
        }
        printToFile(bw);
    }

    private boolean isFinished(int id){
        switch (id){
            case R.id.low_freq:
                return counters[Sensor.TYPE_PROXIMITY] <= 0 && counters[Sensor.TYPE_LIGHT] <= 0;
            case R.id.mid_freq:
                return counters[Sensor.TYPE_ROTATION_VECTOR] <= 0 && counters[Sensor.TYPE_GRAVITY] <= 0;
            case R.id.high_freq:
                return counters[Sensor.TYPE_ACCELEROMETER] <= 0 && counters[Sensor.TYPE_LINEAR_ACCELERATION] <= 0
                        && counters[Sensor.TYPE_MAGNETIC_FIELD] <= 0 && counters[Sensor.TYPE_GYROSCOPE] <= 0;
        }
        return false;
    }

    private BufferedWriter bw;
    private Runnable myStreamingBackgroundTask = new Runnable() {

        @Override
        public void run() {
            try{
                bw = new BufferedWriter(new FileWriter(new File(
                        getExternalFilesDir(null), getFileTitle()), true));
            }
            catch(IOException e){
                e.printStackTrace();
                Toast.makeText(mContext, "Can't open BufferedWriter", Toast.LENGTH_SHORT).show();
            }

            final int counterFull = Integer.parseInt(spinnerTime.getSelectedItem().toString());
            counters = new int[25];
            switch (senzorRadio.getCheckedRadioButtonId()){
                case R.id.low_freq:
                    counters[Sensor.TYPE_PROXIMITY] = counterFull;
                    counters[Sensor.TYPE_LIGHT] = counterFull;
                    break;
                case R.id.mid_freq:
                    counters[Sensor.TYPE_ROTATION_VECTOR] = counterFull;
                    counters[Sensor.TYPE_GRAVITY] = counterFull;
                    break;
                case R.id.high_freq:
                    counters[Sensor.TYPE_ACCELEROMETER] = counterFull;
                    counters[Sensor.TYPE_LINEAR_ACCELERATION] = counterFull;
                    counters[Sensor.TYPE_MAGNETIC_FIELD] = counterFull;
                    counters[Sensor.TYPE_GYROSCOPE] = counterFull;
                    break;
                default:
                    stopThreadFlag = true;
            }

            Log.d("myTHREAD", "BufferedWriter initialized");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    enableRadioGroup(senzorRadio, false);
                    //senzorRadio.setEnabled(false);
                    statusText1.setText("started");
                    statusText2.setText("started");
                    statusText3.setText("started");
                }
            });

            registerListeners();
            //while(counter > 0 && !stopThreadFlag){
            while(!isFinished(senzorRadio.getCheckedRadioButtonId()) && !stopThreadFlag){
                try{
                    Thread.sleep(2000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                    stopThreadFlag = true;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (senzorRadio.getCheckedRadioButtonId()){
                            case R.id.low_freq:
                                statusText1.setText("Proximity " +
                                        (((counterFull - counters[Sensor.TYPE_PROXIMITY]) * 100.0)/counterFull) + "%");
                                statusText2.setText("Light " +
                                        (((counterFull - counters[Sensor.TYPE_LIGHT]) * 100.0)/counterFull) + "%");
                                break;
                            case R.id.mid_freq:
                                statusText1.setText("RotationVector " +
                                        (((counterFull - counters[Sensor.TYPE_ROTATION_VECTOR]) * 100.0)/counterFull) + "%");
                                statusText2.setText("Gravity " +
                                        (((counterFull - counters[Sensor.TYPE_GRAVITY]) * 100.0)/counterFull) + "%");
                                break;
                            case R.id.high_freq:
                                statusText1.setText("Acc/LinAcc " +
                                        (((counterFull - counters[Sensor.TYPE_LINEAR_ACCELERATION]) * 100.0)/counterFull)
                                        + "%/" + (((counterFull - counters[Sensor.TYPE_ACCELEROMETER]) * 100.0)/counterFull) + "%");
                                statusText2.setText("Magnetic " +
                                        (((counterFull - counters[Sensor.TYPE_MAGNETIC_FIELD]) * 100.0)/counterFull) + "%");
                                statusText3.setText("Gyroscope " +
                                        (((counterFull - counters[Sensor.TYPE_GYROSCOPE]) * 100.0)/counterFull) + "%");
                                break;
                        }
                    }
                });
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopThreadFlag = true;
                    btnService.setText("Start");
                    enableRadioGroup(senzorRadio, true);
                    statusText1.setText("");
                    statusText2.setText("sevas");
                    statusText3.setText("");
                }
            });

            myThread = null;
        }
    };

    private void enableRadioGroup(RadioGroup rg, boolean toEnable){
        for (int i = 0; i < rg.getChildCount(); i++) {
            rg.getChildAt(i).setEnabled(toEnable);
        }
    }

    private void printToFile(BufferedWriter bw){
        try{
            bw.flush();
            Log.d("myTHREAD", "line written");
        }
        catch(IOException e){
            e.printStackTrace();
            Toast.makeText(mContext, "Can't write to BufferedWriter", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(myThread != null){
            if(myThread.isAlive()){
                stopThreadFlag = false;
            }
        }
        else{
            stopThreadFlag = true;
        }
        if(stopThreadFlag)
            btnService.setText("Start");
        else
            btnService.setText("Stop");

        //get pre-entered values
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        //spinnerFreq.setSelection(sharedPref.getInt("SEC", 0));
        spinnerTime.setSelection(sharedPref.getInt("TIME", 0));
    }

    //save pre-entered values
    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences.Editor editor = sharedPref.edit();
        //editor.putInt("SEC", spinnerFreq.getSelectedItemPosition());
        editor.putInt("TIME", spinnerTime.getSelectedItemPosition());
        editor.commit();
    }
}