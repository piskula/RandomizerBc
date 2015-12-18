package com.ondro.randomizer.streaming;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
 * Created by Ondro on 08-Dec-15.
 */
public class BackgroundSetUpActivity extends AppCompatActivity implements OnClickListener, SensorEventListener{
    public final String TAG = "BackgroundSetUpActivity";

    private static Button btnService;
    private Context mContext;
    private Spinner spinnerTime;
    private Spinner spinnerFreq;
    private static TextView statusText;
    private Integer[] times = new Integer[]{1,3,5,10,15,20,30,45,60,90,120};
    private Integer[] freq = new Integer[]{50,100,200,500,1000,2000,5000};

    private static boolean stopThreadFlag;
    private static Thread myThread;

    private PowerManager.WakeLock wakeLock;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background_activity);
        mContext = this;

        btnService = (Button) findViewById(R.id.btnService);
        spinnerTime = (Spinner) findViewById(R.id.spinner_time);
        spinnerFreq = (Spinner) findViewById(R.id.spinner_interval);
        statusText = (TextView) findViewById(R.id.statusText);

        btnService.setOnClickListener(this);
        spinnerTime.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, times));
        spinnerFreq.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, freq));

        mySensorManager = (SensorManager) getSystemService(FragmentActivity.SENSOR_SERVICE);
        InitializeSensors();
        registerListeners();
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

    private String getLine(){
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        sb.append(dateFormat.format(cal.getTime()));
        sb.append(".");
        sb.append(cal.get(Calendar.MILLISECOND));
        sb.append("|acc:");
        sb.append(acc01);
        sb.append(",");
        sb.append(acc02);
        sb.append(",");
        sb.append(acc03);
        sb.append("|magn:");
        sb.append(magnetic01);
        sb.append(",");
        sb.append(magnetic02);
        sb.append(",");
        sb.append(magnetic03);
        sb.append("|orient:");
        sb.append(orientation01);
        sb.append(",");
        sb.append(orientation02);
        sb.append(",");
        sb.append(orientation03);
        sb.append("|gyro:");
        sb.append(gyroscope01);
        sb.append(",");
        sb.append(gyroscope02);
        sb.append(",");
        sb.append(gyroscope03);
        sb.append("|light:");
        sb.append(light01);
        sb.append("|proximity:");
        sb.append(proximity01);
        sb.append("|gravity:");
        sb.append(gravity01);
        sb.append(",");
        sb.append(gravity02);
        sb.append(",");
        sb.append(gravity03);
        sb.append("|linacc:");
        sb.append(linAcc01);
        sb.append(",");
        sb.append(linAcc02);
        sb.append(",");
        sb.append(linAcc03);
        sb.append("|rotvec:");
        sb.append(rotVec01);
        sb.append(",");
        sb.append(rotVec02);
        sb.append(",");
        sb.append(rotVec03);
        sb.append(",");
        sb.append(rotVec04);
        sb.append(",");
        sb.append(rotVec05);
        sb.append("|temperature:");
        sb.append(ambientTemperature);
        sb.append("|pressure:");
        sb.append(pressure);
        sb.append("|\r\n");

        return sb.toString();
    }

    //values
    private float acc01;
    private float acc02;
    private float acc03;
    private float linAcc01;
    private float linAcc02;
    private float linAcc03;
    private float rotVec01;
    private float rotVec02;
    private float rotVec03;
    private float rotVec04;
    private float rotVec05;
    private float gravity01;
    private float gravity02;
    private float gravity03;
    private float gyroscope01;
    private float gyroscope02;
    private float gyroscope03;
    private float light01;
    private float proximity01;
    private float magnetic01;
    private float magnetic02;
    private float magnetic03;
    private float orientation01;
    private float orientation02;
    private float orientation03;
    private float pressure;
    private float ambientTemperature;

    private SensorManager mySensorManager;

    private Sensor mySensorAccelerometer;
    private Sensor mySensorLinearAccelerometer;
    private Sensor mySensorRotationVector;
    private Sensor mySensorGravity;
    private Sensor mySensorGyroscope;
    private Sensor mySensorLight;
    private Sensor mySensorProximity;
    private Sensor mySensorMagnetic;
    private Sensor mySensorAmbientTemperature;

    private float[] mGravity;
    private float[] mGeomagnetic;

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
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void refreshOrientation(){
        if(mGravity != null && mGeomagnetic != null){
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                orientation01 = orientation[0];// * 180 / Math.PI);
                orientation02 = orientation[1];// * 180 / Math.PI);
                orientation03 = orientation[2];// * 180 / Math.PI);
            }
        }
    }

    private void registerListeners() {
        mySensorManager.registerListener(this, mySensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorLinearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorRotationVector, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorAmbientTemperature, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values;
                acc01 = event.values[0];
                acc02 = event.values[1];
                acc03 = event.values[2];
                refreshOrientation();
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                linAcc01 = event.values[0];
                linAcc02 = event.values[1];
                linAcc03 = event.values[2];
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                rotVec01 = event.values[0];
                rotVec02 = event.values[1];
                rotVec03 = event.values[2];
                rotVec04 = event.values[3];
                rotVec05 = event.values[4];
                break;
            case Sensor.TYPE_GRAVITY:
                gravity01 = event.values[0];
                gravity02 = event.values[1];
                gravity03 = event.values[2];
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroscope01 = event.values[0];
                gyroscope02 = event.values[1];
                gyroscope03 = event.values[2];
                break;
            case Sensor.TYPE_LIGHT:
                light01 = event.values[0];
                break;
            case Sensor.TYPE_PROXIMITY:
                proximity01 = event.values[0];
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = event.values;
                magnetic01 = event.values[0];
                magnetic02 = event.values[1];
                magnetic03 = event.values[2];
                refreshOrientation();
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                ambientTemperature = event.values[0];
                break;
            case Sensor.TYPE_PRESSURE:
                pressure = event.values[0];
                break;
        }
    }

    private Runnable myStreamingBackgroundTask = new Runnable() {
        private BufferedWriter bw;

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
            Log.d("myTHREAD", "BufferedWriter initialized");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusText.setText("running");
                }
            });

            int freq = Integer.parseInt(spinnerFreq.getSelectedItem().toString());
            int time = Integer.parseInt(spinnerTime.getSelectedItem().toString()) * 60 * 1000;
            int i = time / freq;
            while(i > 0 && !stopThreadFlag){
                long startingPoint = System.currentTimeMillis();

                printToFile(bw);
                takeRestForTimeFrom(freq, startingPoint);

                i--;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopThreadFlag = true;
                    btnService.setText("Start");
                    statusText.setText("");
                }
            });
            myThread = null;
        }
    };

    private void printToFile(BufferedWriter bw){
        try{
            bw.append(getLine());
            bw.flush();
            Log.d("myTHREAD", "line written");
        }
        catch(IOException e){
            e.printStackTrace();
            Toast.makeText(mContext, "Can't write to BufferedWriter", Toast.LENGTH_SHORT).show();
        }
    }

    private void takeRestForTimeFrom(long miliseconds, long startingPoint){
        try{
            Thread.sleep(miliseconds - (System.currentTimeMillis() - startingPoint + 9));
        }
        catch(InterruptedException e){
            e.printStackTrace();
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
        spinnerFreq.setSelection(sharedPref.getInt("SEC", 0));
        spinnerTime.setSelection(sharedPref.getInt("TIME", 0));
    }

    //save pre-entered values
    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SEC", spinnerFreq.getSelectedItemPosition());
        editor.putInt("TIME", spinnerTime.getSelectedItemPosition());
        editor.commit();
    }
}
